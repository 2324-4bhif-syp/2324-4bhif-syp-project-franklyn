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

use image::{ImageFormat, Rgba, RgbaImage};
use reqwest::multipart::{Form, Part};

use fastwebsockets::{handshake, FragmentCollector, Frame, OpCode};
use log::info;

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
    UpdateImage(RgbaImage),
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

pub fn subscribe(server_address: String, pin: u32, username: String, mut current_image: Option<RgbaImage>) -> Subscription<Event> {
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
                    let new_image = handle_message(&server_address, &username, ws, current_image.as_ref()).await;

                    if let Some(image) = new_image {
                        println!("here");
                        current_image = Some(image);
                    }
                    else {
                        let _ = output.send(Event::Nothing).await;
                    }
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
    current_image: Option<&RgbaImage>,
) -> Option<RgbaImage> {
    let msg = match ws.read_frame().await {
        Ok(msg) => msg,
        Err(e) => {
            eprintln!("{e:?}");
            ws.write_frame(Frame::close_raw(vec![].into()))
                .await
                .unwrap();
            return None;
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

            // assume same resolution
            let path_ending = compare_image(current_image, image);

            let file_part = Part::bytes(buf)
                .file_name("image.png")
                .mime_str("image/png")
                .unwrap();

            reqwest::Client::new()
                .post(&format!(
                    "http://{server_address}/screenshot/{username}/{}", path_ending.0
                ))
                .multipart(Form::new().part("image", file_part))
                .send()
                .await
                .unwrap();

            // if we got a new alpha frame set current image to new image
            if path_ending.0 == "alpha" {
                return Some(path_ending.1);
            }

            None
        }
        _ => None,
    }
}

pub fn compare_image(current_image: Option<&RgbaImage>, new_image: RgbaImage) -> (&'static str, RgbaImage) {

    let Some(image) = current_image else { println!("this alpha"); return  ("alpha", new_image)};

    let mut counter = 0;

    let width = image.width();
    let height = image.height();

    let mut output_image = RgbaImage::from_pixel(width, height, Rgba([0, 0, 0, 0]));

    for x in 0..width {
        for y in 0..height {
            let alpha_rgb = image.get_pixel(x, y);
            let beta_rgb = new_image.get_pixel(x, y);

            if alpha_rgb != beta_rgb {
                counter += 1;
                output_image.put_pixel(x, y, beta_rgb.clone());
            }
        }
    }

    // if more than half of the frame is different make it the new alpha frame
    if counter > width * height / 2 {
        println!(" that alpha");
        return ("alpha", new_image);
    }

    println!("dif");

    ("beta", output_image)
}

