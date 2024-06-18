use openbox::{Data, Event};
use iced::executor;
use iced::widget::{button, container, row, column, text, text_input, };
use iced::subscription::Subscription;
use iced::{
    alignment::Vertical, Alignment, Command, 
    Element, Length, Settings, Color, Theme,
    Application, Vector, Shadow, border::Radius,
    Border, Font, font::{Family, Weight}
};
use iced::theme::{Container, Palette};

fn main() -> iced::Result {
    Franklyn::run(Settings::default())
}

enum Franklyn {
    Login(String, String, String),
    Connected(Data),
}

#[derive(Debug, Clone)]
enum Message {
    Ev(Event),

    PinChanged(String),
    LastnameChanged(String),
    FirstnameChanged(String),

    Login(Data),
}

impl Application for Franklyn {
    type Message = Message;
    type Theme = Theme;
    type Executor = executor::Default;
    type Flags = ();

    fn new(_flags: ()) -> (Self, Command<Message>) {
        (
            Self::Login(String::new(), String::new(), String::new()),
            Command::none()
        )
    }

    fn title(&self) -> String {
        String::from("Franklyn")
    }

    fn update(&mut self, msg: Message) -> Command<Message> {
        match self {
            Franklyn::Login(c, f, l) => {
                match msg {
                    Message::PinChanged(cc) => *c = cc,
                    Message::LastnameChanged(lc) => *l = lc,
                    Message::FirstnameChanged(fc) => *f = fc,
                    Message::Login(data) => *self = Franklyn::Connected(data),
                    _ => (),
                }
            }
            Franklyn::Connected(data) => {
                match msg {
                    Message::Ev(e) => {
                        match e {
                            Event::Connected(image) => data.image = image,
                            Event::Disconnected => {
                                data.image = None;
                                eprintln!("ERROR: disconnected");
                            }
                        }
                    }
                    _ => (),
                }
            }
        }

        Command::none()
    }

    fn subscription(&self) -> Subscription<Message> {
        match self {
            Franklyn::Login(_, _, _) => Subscription::none(),
            Franklyn::Connected(data)=> openbox::connect(data.clone()).map(Message::Ev),
        }
    }

    fn view(&self) -> Element<Message> {
        let font = Font { 
            family: Family::Monospace,
            weight: Weight::Semibold,
            ..Font::DEFAULT
        };

        let logo = row![
            container(text("FRAN")
                .size(70)
                .font(font)
            )
            .style(Container::Custom(Box::new(LogoTheme::default()))),
            text("KLYN").font(font).size(70),
        ];

        let content = match self {
            Franklyn::Login(pin, firstname, lastname) => {
                let pin_input = text_input("Pin", &pin)
                    .on_input(Message::PinChanged)
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

                if let Some((pin, firstname, lastname)) = openbox::get_credentials(&pin, &firstname, &lastname) {
                    let data = openbox::Data {
                        pin,
                        firstname,
                        lastname,
                        image: None,
                    };
                    button = button.on_press(Message::Login(data));
                }

                column![pin_input, firstname_input, lastname_input, button]
                    .spacing(10)
                    .align_items(Alignment::Center)
            }
            Franklyn::Connected(data) => {
                column![
                    text(format!("pin: {}", &data.pin)).size(30),
                    row![
                        text(&data.firstname).size(50), 
                        text(&data.lastname).size(50)
                    ]
                    .spacing(20)
                ]
                .align_items(Alignment::Center)
            }
        };

        container(
            column![logo, content]
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

pub struct LogoTheme {
    palatte: Palette,
}

impl Default for LogoTheme {
    fn default() -> Self {
        Self {
            palatte: Palette { 
                background: Color::from([0.596078431372549, 0.7411764705882353, 0.9372549019607843]),
                text: Color::from([1., 1., 1.]),
                primary: Color::from([1., 1., 1.]),
                success: Color::from([1., 1., 1.]),
                danger: Color::from([1., 1., 1.]),
            }
        }
    }
}

impl container::StyleSheet for LogoTheme {
    type Style = Theme;

    fn appearance(&self, _style: &Self::Style) -> container::Appearance {
        container::Appearance {
            text_color: Some(self.palatte.text),
            background: Some(iced::Background::Color(self.palatte.background)),
            border: Border { color: Color::from([1.,1.,1.]), width: 1., radius: Radius::from(1) },
            shadow: Shadow { color: Color::from([1.,1.,1.]), offset: Vector::new(0., 0.), blur_radius: 0. },
        }
    }
}
