use iced::{
    Center, Element, Subscription, Task, Theme, alignment,
    widget::{button, center, column, container, row, text, text_input},
};
use image::RgbaImage;
use openbox::ws::Event;

const _PROD_URL: &str = "franklyn3.htl-leonding.ac.at:8080";
const _DEV_URL: &str = "localhost:8080";

#[derive(Debug, Clone)]
enum Message {
    PinChanged(String),
    FirstnameChanged(String),
    LastnameChanged(String),

    Ev(openbox::ws::Event),
    Connect(u32),
}

#[derive(PartialEq)]
enum ConnectionState {
    Idle,
    Connected,
    Reconnecting(u32),
    Disconnected,
}

struct Openbox<'a> {
    pin: String,
    firstname: String,
    lastname: String,

    connection: ConnectionState,
    server_address: &'a str,
    old_image: Option<RgbaImage>,
}

impl<'a> Openbox<'a> {
    fn new() -> (Self, Task<Message>) {
        (
            Self {
                pin: String::new(),
                firstname: String::new(),
                lastname: String::new(),

                connection: ConnectionState::Idle,
                server_address: _PROD_URL,
                old_image: None,
            },
            Task::none(),
        )
    }

    fn to_username(&self) -> String {
        format!("{}_{}", self.firstname, self.lastname)
    }

    fn update(&mut self, message: Message) -> Task<Message> {
        match message {
            Message::PinChanged(pin) => {
                self.pin = pin;
                Task::none()
            }
            Message::FirstnameChanged(firstname) => {
                self.firstname = firstname;
                Task::none()
            }
            Message::LastnameChanged(lastname) => {
                self.lastname = lastname;
                Task::none()
            }
            Message::Ev(Event::UpdateImage(new_image)) =>{
                self.old_image = Some(new_image);
                Task::none()
            },
            Message::Connect(pin) => {
                self.connection = ConnectionState::Reconnecting(pin);

                Task::none()
            }
            _ => Task::none(),
        }
    }

    fn subscription(&self) -> Subscription<Message> {
        match self.connection {
            ConnectionState::Reconnecting(pin) => {
                openbox::ws::subscribe(self.server_address.to_string(), pin, self.to_username(), self.old_image.clone())
                    .map(Message::Ev)
            }
            ConnectionState::Disconnected => Subscription::none(),
            ConnectionState::Idle | ConnectionState::Connected => Subscription::none(),
        }
    }

    fn view(&self) -> Element<Message> {
        let logo = row![
            container(text("FRAN").size(70))
                .style(|_| openbox::theme::LogoTheme::default().to_style()),
            text("KLYN").size(70),
        ]
        .align_y(Center);

        center(if self.connection == ConnectionState::Idle {
            let pin_input = text_input("pin", &self.pin)
                .on_input(Message::PinChanged)
                .width(300)
                .padding(10);
            let firstname_input = text_input("firstname", &self.firstname)
                .on_input(Message::FirstnameChanged)
                .width(300)
                .padding(10);
            let lastname_input = text_input("lastname", &self.lastname)
                .on_input(Message::LastnameChanged)
                .width(300)
                .padding(10);

            let mut button = button(
                    text("connect").height(40)
                        .align_y(alignment::Vertical::Center)
                        .align_x(alignment::Horizontal::Center)
                )
                .width(300)
                .padding([0, 20]);

            let pin = self.pin.parse::<u32>();

            if pin.is_ok() && !self.firstname.is_empty() && !self.lastname.is_empty() {
                button = button.on_press(Message::Connect(pin.unwrap()));
            }

            column![column![logo].padding([50, 50]), pin_input, firstname_input, lastname_input, button]
                .spacing(10)
                .align_x(Center)
        } else {
            column![
                logo,
                text(format!("Pin: {}", self.pin)).size(30),
                row![
                    text(&self.firstname).size(50),
                    text(&self.lastname).size(50)
                ]
                .spacing(20)
            ]
            .spacing(20)
            .align_x(Center)
        })
        .into()
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
