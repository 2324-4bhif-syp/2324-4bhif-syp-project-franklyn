use std::future::Future;

use bytes::Bytes;
use anyhow::Result;
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
