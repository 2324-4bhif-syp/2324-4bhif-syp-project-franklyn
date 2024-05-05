use iced::executor;
use iced::widget::{button, container, row, column, text, text_input, };
use iced::subscription::Subscription;
use iced::{
    alignment::Vertical, Alignment, Application, 
    Command, Element, Length, Settings, Theme,
};

fn main() -> iced::Result {
    Franklyn::run(Settings::default())
}

enum Franklyn {
    Login(String, String, String),
    Connected(openbox::Data),
}

#[derive(Debug, Clone)]
enum Message {
    Ev(openbox::Event),

    CodeChanged(String),
    LastnameChanged(String),
    FirstnameChanged(String),

    Login(openbox::Data),
}

impl Application for Franklyn {
    type Message = Message;
    type Theme = Theme;
    type Executor = executor::Default;
    type Flags = ();

    fn new(_flags: ()) -> (Franklyn, Command<Message>) {
        (
            Franklyn::Login(String::new(), String::new(), String::new()),
            Command::none(),
        )
    }

    fn title(&self) -> String {
        String::from("Franklyn")
    }

    fn update(&mut self, msg: Message) -> Command<Message> {
        match self {
            Franklyn::Login(c, f, l) => {
                match msg {
                    Message::CodeChanged(cc) => *c = cc,
                    Message::LastnameChanged(lc) => *l = lc,
                    Message::FirstnameChanged(fc) => *f = fc,

                    Message::Login(data) => *self = Franklyn::Connected(data),
                    Message::Ev(e) => {
                        if matches!(e, openbox::Event::Disconnected) {
                            eprintln!("ERROR: disconnected");
                        }
                    }
                }
            }
            Franklyn::Connected(_) => {}
        }

        Command::none()
    }

    fn subscription(&self) -> Subscription<Message> {
        match self {
            Franklyn::Login(_, _, _) => Subscription::none(),
            Franklyn::Connected(data)=> openbox::connect(&data).map(Message::Ev),
        }
    }

    fn view(&self) -> Element<Message> {
        let title = text(self.title());

        let content = match self {
            Franklyn::Login(code, firstname, lastname) => {
                let code_input = text_input("Code", &code)
                    .on_input(Message::CodeChanged)
                    .width(300)
                    .padding(10);

                let firstname_input = text_input("Firstname", &firstname)
                    .on_input(Message::FirstnameChanged)
                    .width(300)
                    .padding(10);

                let lastname_input = text_input("Lastname", &lastname)
                    .on_input(Message::LastnameChanged)
                    .width(300)
                    .padding(10);

                let mut button = button(
                    text("connect")
                        .height(40)
                        .vertical_alignment(Vertical::Center),
                )
                .padding([0, 20]);

                if let Some((code, firstname, lastname)) = openbox::get_credentials(&code, &firstname, &lastname) {
                    button = button.on_press(Message::Login(openbox::Data { code, lastname, firstname }));
                }

                column![code_input, firstname_input, lastname_input, button]
                    .spacing(10)
                    .align_items(Alignment::Center)
            }
            Franklyn::Connected(data) => {
                column![
                    row![text(&data.firstname), text(&data.lastname)],
                    text("Timer?")
                ]
            }
        };

        container(
            column![title, content]
                .padding(20)
                .spacing(20)
                .align_items(Alignment::Center)
        )
        .width(Length::Fill)
        .height(Length::Fill)
        .center_x()
        .center_y()
        .into()
    }
}
