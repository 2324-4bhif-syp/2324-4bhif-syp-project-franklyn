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
use image::{RgbaImage, ImageFormat};

const _PROD_URL : &str = "http://franklyn3.htl-leonding.ac.at:8080";
const _DEV_URL  : &str = "http://localhost:8080";

const URL       : &str = _PROD_URL;

type Ws = FragmentCollector<TokioIo<Upgraded>>;

#[derive(Debug, Clone)]
pub struct Data {
    pub code: u32,
    pub lastname: String,
    pub firstname: String,
    
    pub image: Option<RgbaImage>,
}

enum State {
    Disconnected,
    Connected(Ws),
}

#[derive(Debug, Clone)]
pub enum Event {
    Connected(Option<image::RgbaImage>),
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

pub fn connect(data: Data) -> Subscription<Event> {
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
                            Event::Connected(None)
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
                    _ = output.send(if let Ok(Some(image)) = handle_msg(&user, &data, ws).await {
                        Event::Connected(Some(image))
                    } else {
                        state = State::Disconnected;
                        Event::Disconnected
                    });
                }
            }
        }
    };

    subscription::channel(std::any::TypeId::of::<Connect>(), 100, ws_closure)
}

pub async fn handle_msg(user: &String, _data: &Data, ws: &mut Ws) -> Result<Option<RgbaImage>> {
    let msg = match ws.read_frame().await {
        Ok(msg) => msg,
        Err(e)  => {
            println!("Error: {}", e);
            _ = ws.write_frame(Frame::close_raw(vec![].into())).await?;
            return Ok(None);
        }
    };

    match msg.opcode {
        OpCode::Text | OpCode::Binary => {
            let mut buf = Vec::<u8>::new();

            let image = xcap::Monitor::all()?
                .first().context("ERROR: no monitor found")?
                .capture_image()?;
            image.write_to(&mut std::io::Cursor::new(&mut buf), ImageFormat::Png)?;

            let file_part = multipart::Part::bytes(buf)
                .file_name("image.png")
                .mime_str("image/png")?;

            reqwest::Client::new()
                .post(&format!("{}/screenshot/{user}/alpha", URL))
                .multipart(multipart::Form::new().part("image", file_part))
                .send()
                .await?;
            return Ok(Some(image));
        }
        _ => Ok(None),
    }
}

pub async fn connect_ws(user: &String) -> Result<Ws> {
    let stream = TcpStream::connect(&URL[7..]).await?;

    let req = Request::builder()
        .method("GET")
        .uri(format!("{}/examinee/{}", URL, user))
        .header("Host", URL)
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
