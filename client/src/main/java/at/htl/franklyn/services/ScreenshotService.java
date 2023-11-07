package at.htl.franklyn.services;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.AWTException;
import java.awt.image.BufferedImage;

public class ScreenshotService {

    public static BufferedImage getScreenshot() {
        BufferedImage screenShot;

        try {
            screenShot = new Robot().createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            );
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        return screenShot;
    }
}
