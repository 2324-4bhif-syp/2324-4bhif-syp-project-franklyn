use iced::executor;
use iced::widget::{
    row,
    button, column, container, progress_bar, text, text_input, Column,
};
use iced::{
    alignment::Vertical,
    Alignment, Application, Command, Element, Length, Settings, Subscription,
    Theme,
};

use fastwebsockets::FragmentCollector;
use hyper::upgrade::Upgraded;
use hyper_util::rt::TokioIo;

fn main() -> iced::Result {
    App::run(Settings::default())
}

pub struct App {
    code: String,
    firstname: String,
    lastname: String,
    session: Option<FragmentCollector<TokioIo<Upgraded>>>
}

#[derive(Debug, Clone)]
pub enum Message {
    CodeChanged(String),
    FirstnameChanged(String),
    LastnameChanged(String),
    Login((u32, String, String)),
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
                session: None::<FragmentCollector<TokioIo<Upgraded>>>,
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
            Message::Login((code, firstname, lastname)) => {

                println!("{code}");
            }
        }
        Command::none()
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
