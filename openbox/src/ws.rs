use anyhow::Result;
use bytes::{Bytes, BytesMut};
use fastwebsockets::{handshake, FragmentCollector, Frame, OpCode, Payload};
use futures::{SinkExt, StreamExt};
use http_body_util::Empty;
use hyper::header::{CONNECTION, UPGRADE};
use hyper::upgrade::Upgraded;
use hyper::Request;
use hyper_util::rt::TokioIo;
use iced::{futures::channel::mpsc, stream, Subscription};
use image::RgbaImage;
use reqwest::multipart::Form;
use serde::Deserialize;
use std::collections::HashMap;
use std::future::Future;
use std::time::{SystemTime, UNIX_EPOCH};
use clipboard_rs::{Clipboard, ClipboardContent, ClipboardContext};
use tokio::net::TcpStream;
use tokio::task;

use crate::screen;

struct SpawnExecutor;

impl<Fut> hyper::rt::Executor<Fut> for SpawnExecutor
where
    Fut: Future + Send + 'static,
    Fut::Output: Send + 'static,
{
    fn execute(&self, fut: Fut) {
        tokio::task::spawn(fut);
    }
}

pub enum State {
    Connected(FragmentCollector<TokioIo<Upgraded>>),
    Disconnected,
}

#[derive(Debug, Clone)]
pub enum Event {
    Connected,
    Reconnect,
    Disconnect,
    ServerError,
    Received(WsMessage),
}

#[derive(Debug, Clone, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum WsMessage {
    #[serde(rename = "CAPTURE_SCREEN")]
    CaptureScreen {
        frame_type: FrameType,
    },
    #[serde(rename = "DISCONNECT")]
    Disconnect,

    // used for process_screenshots to update session_id
    SetId(String),

    // used to abort task
    Cancel,
}

#[derive(Debug, Clone, Deserialize)]
#[serde(rename_all = "UPPERCASE")]
pub enum FrameType {
    Alpha,
    Beta,
    Unspecified,
}

pub enum Connection {
    Upgrade(FragmentCollector<TokioIo<Upgraded>>, String),
    InvalidPin,
    ServerError,
}

pub async fn connect(
    pin: &str,
    server: &str,
    firstname: &str,
    lastname: &str,
) -> Result<Connection> {
    let stream = TcpStream::connect(&server).await?;

    let res = reqwest::Client::new()
        .post(format!("http://{server}/exams/join/{pin}"))
        .json(&HashMap::<&str, &str>::from_iter([
            ("firstname", firstname),
            ("lastname", lastname),
        ]))
        .send()
        .await?;

    let Some(location) = res.headers().get("location") else {
        return Ok(Connection::InvalidPin);
    };
    let location = location.to_str()?;

    let Some(session_id) = location.split('/').last() else {
        return Ok(Connection::ServerError);
    };

    let req = Request::builder()
        .method("GET")
        .uri(location)
        .header("Host", server)
        .header(UPGRADE, "websocket")
        .header(CONNECTION, "upgrade")
        .header("Sec-WebSocket-Key", handshake::generate_key())
        .header("Sec-WebSocket-Version", "13")
        .body(Empty::<Bytes>::new())?;

    let (mut ws, _) = handshake::client(&SpawnExecutor, req, stream).await?;
    ws.set_auto_pong(false);
    Ok(Connection::Upgrade(
        FragmentCollector::new(ws),
        session_id.to_string(),
    ))
}

pub fn subscribe(
    pin: String,
    server: String,
    firstname: String,
    lastname: String,
) -> Subscription<Event> {
    let connection_cloj = stream::channel(100, move |mut output| async move {
        let mut state = State::Disconnected;

        let (mut sender, receiver) = mpsc::channel(5);
        let s = server.clone();
        task::spawn(async move { process_screenshots(s, receiver).await });

        loop {
            match &mut state {
                State::Disconnected => {
                    let Ok(connection) = connect(&pin, &server, &firstname, &lastname).await else {
                        tokio::time::sleep(tokio::time::Duration::from_secs(1)).await;
                        _ = output.send(Event::Reconnect).await;
                        continue;
                    };

                    match connection {
                        Connection::Upgrade(ws, session) => {
                            _ = output.send(Event::Connected).await;
                            _ = sender.send(WsMessage::SetId(session)).await;
                            state = State::Connected(ws);
                        }
                        Connection::InvalidPin | Connection::ServerError => {
                            _ = sender.send(WsMessage::Cancel).await;
                            _ = output.send(Event::ServerError).await;
                            // return;
                        }
                    }
                }
                State::Connected(ws) => match handle_message(ws).await {
                    Event::Received(ws_msg) => _ = sender.send(ws_msg).await,
                    Event::Reconnect => state = State::Disconnected,
                    event => _ = output.send(event).await,
                },
            }
        }
    });

    struct Connect;
    Subscription::run_with_id(std::any::TypeId::of::<Connect>(), connection_cloj)
}

pub async fn handle_message(ws: &mut FragmentCollector<TokioIo<Upgraded>>) -> Event {
    let msg = match ws.read_frame().await {
        Ok(msg) => msg,
        Err(_e) => {
            let _ = ws.write_frame(Frame::close_raw(vec![].into())).await;
            return Event::Reconnect;
        }
    };

    match msg.opcode {
        OpCode::Ping => {
            let _ = ws.write_frame(Frame::pong(msg.payload)).await;
            return Event::Connected;
        },
        _ => {}
    }

    match msg.payload {
        Payload::Bytes(buf) => {
            let raw = buf.iter().map(|&b| b as char).collect::<String>();
            let msg = serde_json::from_str::<WsMessage>(&raw);

            match msg {
                Err(_) => Event::Reconnect,
                Ok(WsMessage::Disconnect) => Event::Disconnect,
                Ok(msg) => Event::Received(msg),
            }
        }
        _ => Event::Connected,
    }
}

async fn process_screenshots(server: String, mut receiver: mpsc::Receiver<WsMessage>) {
    let mut session = String::new();
    let mut cur_img = None::<RgbaImage>;
    let clipboard = ClipboardContext::new().unwrap();
    let mut is_too_long = false;

    loop {
        is_too_long = false;
        let content = clipboard.get_text().unwrap_or("empty".to_string());

        if content.lines().count() > 5 {
            is_too_long = true;
            println!("SUS: {}", content);
        }
        
        let msg = receiver.select_next_some().await;

        let (file_part, image, option) = match msg {
            WsMessage::Cancel => return,
            WsMessage::SetId(id) => {
                session = id;
                continue;
            }
            WsMessage::CaptureScreen { frame_type } => match frame_type {
                FrameType::Alpha => screen::take_screenshot(true, cur_img.as_ref()),
                FrameType::Beta | FrameType::Unspecified => {
                    screen::take_screenshot(false, cur_img.as_ref())
                }
            },
            _ => unreachable!(),
        };

        let Ok(file_part) = file_part else {
            eprintln!("ERROR: unable to produce screenshot err={file_part:?}");
            continue;
        };

        let path = format!(
            "http://{}/telemetry/by-session/{}/screen/upload/{}",
            server, session, option,
        );

        if let Err(e) = reqwest::Client::new()
            .post(path)
            .multipart(Form::new()
                .part("image", file_part)
                .text("sus", is_too_long.to_string())
            )
            .send()
            .await
        {
            eprintln!("{:?}", e);
        }

        cur_img = Some(image);
    }
}
