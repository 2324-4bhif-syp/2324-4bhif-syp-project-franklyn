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
            }
            OpCode::Close => {
                break;
            }
            _ => {}
        }
    }

    Ok(())
}
