use iced::executor;
use iced::widget::{
    row,
    button, column, container, progress_bar, text, text_input, Column,
};
use iced::subscription::{self, Subscription};
use iced::{
    alignment::Vertical,
    Alignment, Application, Command, Element, Length, Settings, Subscription as S,
    Theme,
};

use futures::channel::mpsc;
use futures::sink::SinkExt;
use futures::stream::StreamExt;

use async_tungstenite::tungstenite;

fn main() -> iced::Result {
    App::run(Settings::default())
}

pub struct App {
    code: String,
    firstname: String,
    lastname: String,
    state: State,
}

#[derive(Debug, Clone)]
pub enum InternalMessage {
    Connected,
    Disconnected,
    User(String),
}

#[derive(Debug)]
#[allow(clippy::large_enum_variant)]
pub enum InternalState {
    Disconnected,
    Connected(
        async_tungstenite::WebSocketStream<
            async_tungstenite::tokio::ConnectStream,
        >,
        mpsc::Receiver<InternalMessage>,
    ),
}

impl std::fmt::Display for InternalMessage {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            InternalMessage::Connected => write!(f, "Connected successfully!"),
            InternalMessage::Disconnected => {
                write!(f, "Connection lost... Retrying...")
            }
            InternalMessage::User(message) => write!(f, "{message}"),
        }
    }
}

#[derive(Debug, Clone)]
struct Connection(mpsc::Sender<InternalMessage>);

impl Connection {
    pub fn send(&mut self, message: InternalMessage) {
        self.0
            .try_send(message)
            .expect("Send message to franklyn server");
    }
}

#[derive(Debug, Clone)]
enum State {
    Disconnected,
    Connected(Connection),
}

#[derive(Debug, Clone)]
pub enum Event {
    Connected(Connection),
    Disconnected,
    MessageReceived(InternalMessage),
}

#[derive(Debug, Clone)]
pub enum Message {
    CodeChanged(String),
    FirstnameChanged(String),
    LastnameChanged(String),
    Echo(Event),
    Login((u32, String, String)),
}

pub fn connect() -> Subscription<Event> {
    struct Connect;

    subscription::channel(
        std::any::TypeId::of::<Connect>(),
        100,
        |mut output| async move {
            let mut state = InternalState::Disconnected;

            loop {
                match &mut state {
                    InternalState::Disconnected => {
                        const FRANKLYN: &str = "ws://localhost:8080/examinee/tobias";

                        match async_tungstenite::tokio::connect_async(FRANKLYN).await {
                            Ok((ws, _)) => {
                                let (sender, receiver) = mpsc::channel(100);

                                _ = output
                                    .send(Event::Connected(Connection(sender)))
                                    .await;

                                state = InternalState::Connected(ws, receiver);
                            }
                            Err(_) => {
                                tokio::time::sleep(tokio::time::Duration::from_secs(1)).await;
                                _ = output.send(Event::Disconnected).await;
                            }
                        }
                    }
                    InternalState::Connected(ws, input) => {
                        let mut fused_ws = ws.by_ref().fuse();

                        futures::select! {
                            received = fused_ws.select_next_some() => {
                                match received {
                                    Ok(tungstenite::Message::Text(msg)) => {
                                        _ = output.send(Event::MessageReceived(InternalMessage::User(msg)))
                                            .await;
                                    }
                                    Err(_) => {
                                        _ = output.send(Event::Disconnected).await;
                                        state = InternalState::Disconnected;
                                    }
                                    Ok(_) => continue,
                                }
                            }

                            message = input.select_next_some() => {
                                let result = ws.send(tungstenite::Message::Text(message.to_string())).await;

                                if result.is_err() {
                                    _ = output.send(Event::Disconnected).await;
                                    state = InternalState::Disconnected;
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

impl App {
    fn get_credentials(&self) -> Option<(u32, String, String)> {
        if self.firstname.is_empty() || self.lastname.is_empty() || self.code.len() != 4 {
            return None;
        }

        Some((
            self.code.parse::<u32>().ok()?,
            self.firstname.clone(),
            self.lastname.clone(),
        ))
    }
}

impl Application for App {
    type Message = Message;
    type Theme = Theme;
    type Executor = executor::Default;
    type Flags = ();

    fn new(_flags: ()) -> (Self, Command<Message>) {
        (
            App {
                code: String::new(),
                firstname: String::new(),
                lastname: String::new(),
                state: State::Disconnected,
            },
            Command::none(),
        )
    }

    fn title(&self) -> String {
        String::from("Franklyn")
    }

    fn update(&mut self, msg: Message) -> Command<Message> {
        match msg {
            Message::CodeChanged(c) => self.code = c,
            Message::FirstnameChanged(f) => self.firstname = f,
            Message::LastnameChanged(l) => self.lastname = l,
            Message::Echo(ev) => {

            }
            Message::Login((code, firstname, lastname)) => {
                println!("{code}");
            }
        }
        Command::none()
    }

    fn subscription(&self) -> Subscription<Message> {
        connect().map(Message::Echo)
    }

    fn view(&self) -> Element<Message> {
        let title = text(self.title());

        let credentials_input = {
            let code_input = text_input("Code", &self.code)
                .on_input(Message::CodeChanged)
                .padding(10);

            let firstname_input = text_input("Firstname", &self.firstname)
                .on_input(Message::FirstnameChanged)
                .padding(10);

            let lastname_input = text_input("Lastname", &self.lastname)
                .on_input(Message::LastnameChanged)
                .padding(10);

            let mut button = button(
                text("connect")
                    .height(40)
                    .vertical_alignment(Vertical::Center),
            )
            .padding([0, 20]);

            if let Some(credentials) = self.get_credentials() {
                button = button.on_press(Message::Login(credentials));
            }

            column![code_input, firstname_input, lastname_input, button]
                .spacing(10)
                .align_items(Alignment::Center)
        };

        column![title, credentials_input]
            .width(Length::Fill)
            .height(Length::Fill)
            .padding(20)
            .spacing(20)
            .into()
    }
}
