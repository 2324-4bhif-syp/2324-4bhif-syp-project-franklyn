use iced::futures::StreamExt;
use iced::widget::{button, container, row, column, text, text_input, };
use iced::{color, Theme, Subscription, Element, Font, Task};

const _PROD_URL : &str = "franklyn3.htl-leonding.ac.at:8080";
const _DEV_URL  : &str = "localhost:8080";

#[derive(Debug, Clone)]
enum Message {
    PinChanged(String),
    UsernameChanged(String),

    Ev(openbox::ws::Event),
    Connect(u32),
}

#[derive(PartialEq)]
enum ConnectionState {
    Idle,
    Connected,
    Reconnecting,
    Disconnected,
}

struct Openbox<'a> {
    pin: String,
    username: String,

    connection: ConnectionState,
    server_address: &'a str,
}

impl<'a> Openbox<'a> {
    fn new() -> (Self, Task<Message>) {
        (
            Self {
                pin: String::new(),
                username: String::new(),

                connection: ConnectionState::Idle,
                server_address: _DEV_URL,
            },
            Task::none()
        )
    }

    fn update(&mut self, message: Message) -> Task<Message> {
        match message {
            Message::PinChanged(pin) => {
                self.pin = pin;
                Task::none()
            }
            Message::UsernameChanged(username) => {
                self.username = username;
                Task::none()
            }
            Message::Ev(_) => {
                println!("ev");
                Task::none()
            }
            Message::Connect(pin) => {
                println!("{pin}: {}", self.username);
                self.connection = ConnectionState::Reconnecting;

                Task::none()
            }
        }
    }

    fn subscription(&self) -> Subscription<Message> {
        println!("sub");

        match self.connection {
            ConnectionState::Connected
            | ConnectionState::Reconnecting
            | ConnectionState::Disconnected
            => openbox::ws::subscribe(
                self.server_address.to_string(),
                self.pin.parse::<u32>().unwrap(),
                self.username.clone()).map(Message::Ev),
            ConnectionState::Idle => Subscription::none(),
        }
    }

    fn view(&self) -> Element<Message> {
        let font = Font::default();

        let logo = row![
            container(text("FRAN").size(70).font(font)),
            text("KLYN").size(70).font(font).color(color!(0x0000ff)),
        ];

        if self.connection == ConnectionState::Idle {
            let pin_input = text_input("pin", &self.pin)
                .on_input(Message::PinChanged).width(300).padding(10);
            let username_input = text_input("username", &self.username)
                .on_input(Message::UsernameChanged).width(300).padding(10);

            let mut button = button(text("connect").height(40)).padding([0, 20]);

            let pin = self.pin.parse::<u32>();

            if pin.is_ok() && !self.username.is_empty() {
                button = button.on_press(Message::Connect(pin.unwrap()));
            }

            column![logo, pin_input, username_input, button].into()
        } else {
            column![logo].into()
        }
    }

    fn theme(&self) -> Theme {
        Theme::default()
    }
}

pub fn main() -> iced::Result {
    iced::application("Openbox", Openbox::update, Openbox::view)
        .theme(Openbox::theme)
        .subscription(Openbox::subscription)
        .run_with(Openbox::new)
}

