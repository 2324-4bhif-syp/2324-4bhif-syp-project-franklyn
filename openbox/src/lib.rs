use std::future::Future;

use bytes::Bytes;
use reqwest::multipart;
use anyhow::{Result, Context};
use fastwebsockets::{Frame, OpCode, FragmentCollector};
use http_body_util::Empty;
use hyper::{header::{CONNECTION, UPGRADE}, upgrade::Upgraded, Request};
use hyper_util::rt::TokioIo;
use tokio::net::TcpStream;
use iced::subscription::{self, Subscription};
use futures::channel::mpsc;
use futures::sink::SinkExt;

const _PROD_URL:  &str = "http://franklyn.ddns.net:8080";
const DEV_URL:   &str = "http://localhost:8080";

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

pub fn connect(data: &Data) -> Subscription<Event> {
    struct Connect;
    let user = format!("{}_{}", data.firstname, data.lastname);

    let ws_closure = |mut output: mpsc::Sender<_>| async move {
        let mut state = State::Disconnected;

        loop {
            match &mut state {
                State::Disconnected => {
                    _ = output.send(match connect_ws(&user).await {
                        Ok(ws) => {
                            state = State::Connected(ws);
                            Event::Connected
                        }
                        Err(e) => {
                            eprintln!("e: {e:?}");
                            tokio::time::sleep(tokio::time::Duration::from_secs(1)).await;
                            state = State::Disconnected;
                            Event::Disconnected
                        }
                    }).await;
                }
                State::Connected(ref mut ws) => {
                    if handle_msg(&user, ws).await.is_ok_and(|k| !k) {
                        _ = output.send(Event::Disconnected).await;
                        state = State::Disconnected;
                    }
                }
            }
        }
    };

    subscription::channel(std::any::TypeId::of::<Connect>(), 100, ws_closure)
}

pub async fn handle_msg(user: &String, ws: &mut FragmentCollector<TokioIo<Upgraded>>) -> Result<bool> {
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
            let mut buf = Vec::<u8>::new();

            let image = xcap::Monitor::all()?
                .first().context("ERROR: no monitor found")?
                .capture_image()?;
            image.write_to(&mut std::io::Cursor::new(&mut buf), image::ImageFormat::Png)?;

            let file_part = multipart::Part::bytes(buf)
                .file_name("image.png")
                .mime_str("image/png")?;

            reqwest::Client::new()
                .post(&format!("{}/screenshot/{user}/alpha", DEV_URL))
                .multipart(multipart::Form::new().part("image", file_part))
                .send()
                .await?;
        }
        OpCode::Close => return Ok(false),
        _ => (),
    }

    Ok(true)
}

pub async fn connect_ws(user: &String) -> Result<FragmentCollector<TokioIo<Upgraded>>> {
    let stream = TcpStream::connect(&DEV_URL[7..]).await?;

    let req = Request::builder()
        .method("GET")
        .uri(format!("{}/examinee/{}", DEV_URL, user))
        .header("Host", DEV_URL)
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
