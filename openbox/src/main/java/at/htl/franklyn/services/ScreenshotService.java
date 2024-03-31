package at.htl.franklyn.services;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotService {

    private static BufferedImage alphaFrame = null;

    public static Response getScreenshot() {
        BufferedImage screenshot;

        Response.ResponseBuilder r = Response.ok();

        try {
            screenshot = new Robot().createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            );

            if (null == alphaFrame){
                alphaFrame = screenshot;

                r.header("Frame-Type", "alpha");

                return r.entity(screenshot).build();
            }

            return r.entity(getDifference(screenshot, r)).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getDifference(BufferedImage betaFrame, Response.ResponseBuilder response) {

        int width = alphaFrame.getWidth();
        int height = alphaFrame.getHeight();

        // schauen ob ein hund mitten im test auflösung ändert

        BufferedImage outputFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int counter = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int alphaRGB = alphaFrame.getRGB(x, y);
                int betaRGB = betaFrame.getRGB(x, y);

                if (alphaRGB != betaRGB) {
                    counter++;
                    outputFrame.setRGB(x, y, betaRGB);
                } else {
                    outputFrame.setRGB(x, y, 0);
                }
            }
        }

        // if more than half of the frame is different make it the new alpha frame
        if(counter > width * height * 0.5){
            alphaFrame = betaFrame;
            response.header("Frame-Type", "alpha");
            return betaFrame;
        }

        response.header("Frame-Type", "difference");

        return outputFrame;
    }
}
