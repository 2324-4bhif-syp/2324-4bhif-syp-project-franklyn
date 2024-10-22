use iced::{Border, Color, Shadow, Vector, theme::Palette, border::Radius, widget::container::Style};

pub struct LogoTheme {
    palette: Palette,
}

impl Default for LogoTheme {
    fn default() -> LogoTheme {
        let palette = Palette {
            background: Color::from([0.596078431372549, 0.7411764705882353, 0.9372549019607843]),
            text: Color::from([1., 1., 1.]),
            primary: Color::from([1., 1., 1.]),
            success: Color::from([1., 1., 1.]),
            danger: Color::from([1., 1., 1.]),
        };

        Self { palette }
    }
}

impl LogoTheme {
    pub fn to_style(&self) -> Style {
        Style {
            text_color: Some(self.palette.text),
            background: Some(iced::Background::Color(self.palette.background)),
            border: Border {
                color: Color::from([1., 1., 1.]),
                width: 1.,
                radius: Radius::from(1),
            },
            shadow: Shadow {
                color: Color::from([1., 1., 1.]),
                offset: Vector::new(0., 0.),
                blur_radius: 0.,
            },
        }
    }
}
