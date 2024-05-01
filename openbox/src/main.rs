use openbox::connect;

use anyhow::Result;
use fastwebsockets::{Frame, OpCode};

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
