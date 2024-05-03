/*
use std::future::Future;

use bytes::Bytes;
use anyhow::Result;
use fastwebsockets::{Frame, OpCode};
use fastwebsockets::FragmentCollector;
use http_body_util::Empty;
use hyper::{header::{CONNECTION, UPGRADE}, upgrade::Upgraded, Request};
use hyper_util::rt::TokioIo;
use tokio::net::TcpStream;

const DOMAIN: &str = "localhost:8080";

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

pub async fn connect(path: &str) -> Result<FragmentCollector<TokioIo<Upgraded>>> {
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

#[tokio::main(flavor = "current_thread")]
async fn main() -> Result<()> {
    let mut ws = connect("examinee/tobias").await?;
    let client = reqwest::Client::new();

    loop {
        let msg = match ws.read_frame().await {
            Ok(msg) => msg,
            Err(e)  => {
                println!("Error: {}", e);
                ws.write_frame(Frame::close_raw(vec![].into())).await?;
                break;
            }
        };

        match msg.opcode {
            OpCode::Text | OpCode::Binary => {
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

                let res = client.post("http://localhost:8080/screenshot/tobias/alpha")
                    .multipart(form)
                    .send()
                    .await?;

                println!("{res:?}");

            }
            OpCode::Close => {
                break;
            }
            _ => {}
        }
    }

    Ok(())
}
*/
