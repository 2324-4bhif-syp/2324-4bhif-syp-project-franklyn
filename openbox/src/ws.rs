use iced::{stream, Subscription};

use futures::SinkExt;
use std::future::Future;
use tokio::net::TcpStream;

use anyhow::{Context, Result};
use bytes::Bytes;

use http_body_util::Empty;
use hyper::header::{CONNECTION, UPGRADE};
use hyper::upgrade::Upgraded;
use hyper::Request;
use hyper_util::rt::TokioIo;

use image::ImageFormat;
use reqwest::multipart::{Form, Part};

use fastwebsockets::{handshake, FragmentCollector, Frame, OpCode};

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
    Disconnected,
    Connected(FragmentCollector<TokioIo<Upgraded>>),
}

#[derive(Debug, Clone)]
pub enum Event {
    Nothing,
}

pub async fn connect(
    server_address: &str,
    user: &str,
) -> Result<FragmentCollector<TokioIo<Upgraded>>> {
    let stream = TcpStream::connect(&server_address).await?;

    let req = Request::builder()
        .method("GET")
        .uri(format!("http://{}/examinee/{}", server_address, user))
        .header("Host", server_address)
        .header(UPGRADE, "websocket")
        .header(CONNECTION, "upgrade")
        .header("Sec-WebSocket-Key", handshake::generate_key())
        .header("Sec-WebSocket-Version", "13")
        .body(Empty::<Bytes>::new())?;

    let (ws, _) = handshake::client(&SpawnExecutor, req, stream).await?;
    Ok(FragmentCollector::new(ws))
}

pub fn subscribe(server_address: String, pin: u32, username: String) -> Subscription<Event> {
    let connection_cloj = stream::channel(100, move |mut output| async move {
        let mut state = State::Disconnected;

        loop {
            match &mut state {
                State::Disconnected => match connect(&server_address, &username).await {
                    Ok(ws) => {
                        state = State::Connected(ws);
                        let _ = output.send(Event::Nothing).await;
                    }
                    Err(e) => {
                        eprintln!("{e:?}");

                        tokio::time::sleep(tokio::time::Duration::from_secs(1)).await;
                        let _ = output.send(Event::Nothing).await;
                    }
                },
                State::Connected(ws) => {
                    handle_message(&server_address, &username, ws).await;
                    let _ = output.send(Event::Nothing).await;
                }
            }
        }
    });

    struct Connect;
    Subscription::run_with_id(std::any::TypeId::of::<Connect>(), connection_cloj)
}

pub async fn handle_message(
    server_address: &str,
    username: &str,
    ws: &mut FragmentCollector<TokioIo<Upgraded>>,
) {
    let msg = match ws.read_frame().await {
        Ok(msg) => msg,
        Err(e) => {
            eprintln!("{e:?}");
            ws.write_frame(Frame::close_raw(vec![].into()))
                .await
                .unwrap();
            return;
        }
    };

    match msg.opcode {
        OpCode::Text | OpCode::Binary => {
            let mut buf = Vec::<u8>::new();

            let image = xcap::Monitor::all()
                .unwrap()
                .first()
                .context("ERROR: no monitor found")
                .unwrap()
                .capture_image()
                .unwrap();
            image
                .write_to(&mut std::io::Cursor::new(&mut buf), ImageFormat::Png)
                .unwrap();

            let file_part = Part::bytes(buf)
                .file_name("image.png")
                .mime_str("image/png")
                .unwrap();

            reqwest::Client::new()
                .post(&format!(
                    "http://{server_address}/screenshot/{username}/alpha"
                ))
                .multipart(Form::new().part("image", file_part))
                .send()
                .await
                .unwrap();
        }
        _ => (),
    }
}
