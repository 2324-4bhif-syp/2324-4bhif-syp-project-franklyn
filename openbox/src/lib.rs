use std::future::Future;

use bytes::Bytes;
use anyhow::Result;
use fastwebsockets::{Frame, OpCode};
use fastwebsockets::FragmentCollector;
use http_body_util::Empty;
use hyper::{header::{CONNECTION, UPGRADE}, upgrade::Upgraded, Request};
use hyper_util::rt::TokioIo;
use tokio::net::TcpStream;
use iced::subscription::{self, Subscription};
use futures::channel::mpsc;
use futures::sink::SinkExt;

const DOMAIN: &str = "localhost:8080";

#[derive(Debug, Clone)]
pub struct Data {
    pub code: u32,
    pub lastname: String,
    pub firstname: String,
}

enum State {
    Disconnected,
    Connected(FragmentCollector<TokioIo<Upgraded>>),
}

#[derive(Debug, Clone, Copy)]
pub enum Event {
    Connected,
    Disconnected,
}

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

pub fn connect(Data { code, lastname, firstname }: &Data) -> Subscription<Event> {
    let _code = code;
    let path = format!("examinee/{firstname}_{lastname}");
    struct Connect;

    let ws_closure = |mut output: mpsc::Sender<_>| async move {
        let mut state = State::Disconnected;

        loop {
            match &mut state {
                State::Disconnected => {
                    if let Ok(ws) = connect_ws(&path).await {
                        _ = output.send(Event::Connected).await;
                        state = State::Connected(ws);
                    } else {
                        println!("Failed");
                        tokio::time::sleep(tokio::time::Duration::from_secs(1)).await;
                        _ = output.send(Event::Disconnected).await;
                    }
                }
                State::Connected(ref mut ws) => {
                    if handle_msg(ws).await.is_ok_and(|k| !k) {
                        _ = output.send(Event::Disconnected).await;
                        state = State::Disconnected;
                    }
                }
            }
        }
    };

    subscription::channel(std::any::TypeId::of::<Connect>(), 100, ws_closure)
}

pub async fn handle_msg(ws: &mut FragmentCollector<TokioIo<Upgraded>>) -> Result<bool> {
    let msg = match ws.read_frame().await {
        Ok(msg) => msg,
        Err(e)  => {
            println!("Error: {}", e);
            _ = ws.write_frame(Frame::close_raw(vec![].into())).await?;
            return Ok(false);
        }
    };

    match msg.opcode {
        OpCode::Text | OpCode::Binary => {
            println!("{:?}", msg.payload);
            let mut buf = Vec::<u8>::new();

            xcap::Monitor::all().unwrap()
                .first().unwrap()
                .capture_image().unwrap()
                .write_to(
                    &mut std::io::Cursor::new(&mut buf),
                    image::ImageFormat::Png,
                )?;

            let file_part = reqwest::multipart::Part::bytes(buf)
                .file_name("image.png")
                .mime_str("image/png")
                .unwrap();

            let form = reqwest::multipart::Form::new().part("image", file_part);

            let res = reqwest::Client::new().post("http://localhost:8080/screenshot/tobias/alpha")
                .multipart(form)
                .send()
                .await?;

            println!("{res:?}");
        }
        OpCode::Close => return Ok(false),
        _ => (),
    }

    Ok(true)
}

pub async fn connect_ws(path: &str) -> Result<FragmentCollector<TokioIo<Upgraded>>> {
    let stream = TcpStream::connect(DOMAIN).await?;

    let req = Request::builder()
        .method("GET")
        .uri(format!("http://{}/{}", DOMAIN, path))
        .header("Host", DOMAIN)
        .header(UPGRADE, "websocket")
        .header(CONNECTION, "upgrade")
        .header(
            "Sec-WebSocket-Key",
            fastwebsockets::handshake::generate_key(),
        )
        .header("Sec-WebSocket-Version", "13")
        .body(Empty::<Bytes>::new())?;

    let (ws, _) = fastwebsockets::handshake::client(&SpawnExecutor, req, stream).await?;
    Ok(FragmentCollector::new(ws))
}

pub fn get_credentials<'a>(code: &'a str, firstname: &'a str, lastname: &'a str) -> Option<(u32, String, String)> {
    if firstname.is_empty() || lastname.is_empty() || code.len() != 3 {
        return None;
    }

    Some((
        code.parse::<u32>().ok()?,
        firstname.to_string(),
        lastname.to_string(),
    ))
}
