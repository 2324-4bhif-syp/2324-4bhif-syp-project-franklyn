package at.htl.franklyn.Services;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenshotService {

    public static BufferedImage getScreenshot(){

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

    public static BufferedImage getScreenshot(int width, int height){

        BufferedImage screenShot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Image scaledImage = getScreenshot().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        screenShot.getGraphics().drawImage(scaledImage, 0, 0, null);

        return screenShot;
    }
}
