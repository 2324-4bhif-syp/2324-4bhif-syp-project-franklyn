package at.htl.franklyn.services;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenshotService {

    private static BufferedImage alphaFrame = null;

    public static Response getScreenshot() {
        BufferedImage screenshot;

        Response.ResponseBuilder r = Response.ok();

        try {
            screenshot = new Robot().createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            );

            int width = screenshot.getWidth();
            int height = screenshot.getHeight();

            Log.info(width + "-" + height);

            if (width != 1920 || height != 1080) {
                // scale image to 1920*1080 if it has a different size

                Log.info("resize needed");

                BufferedImage resizedImage = new BufferedImage(
                        1920,
                        1080,
                        BufferedImage.TYPE_INT_RGB
                );

                Graphics graphicsOfNewImage = resizedImage.getGraphics();

                graphicsOfNewImage.drawImage(screenshot, 0, 0, 1920, 1080, null);

                screenshot = resizedImage;

                graphicsOfNewImage.dispose();
                resizedImage.flush();
            }

            if (null == alphaFrame) {
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
        if (counter > width * height * 0.5) {
            alphaFrame = betaFrame;
            response.header("Frame-Type", "alpha");
            return betaFrame;
        }

        response.header("Frame-Type", "difference");

        return outputFrame;
    }
}
