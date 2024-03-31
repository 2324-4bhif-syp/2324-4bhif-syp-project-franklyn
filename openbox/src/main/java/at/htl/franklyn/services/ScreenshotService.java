package at.htl.franklyn.services;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotService {
    public static Response getScreenshot() {
        BufferedImage screenshot;

        Response.ResponseBuilder r = Response.ok();

        try {
            screenshot = new Robot().createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            );

            File alphaFrame = new File("alpha-frame.png");

            if (!alphaFrame.exists()){
                ImageIO.write(screenshot, "png", alphaFrame);

                r.header("Frame-Type", "alpha");

                return r.entity(screenshot).build();
            }

            return r.entity(getDifference(alphaFrame.getPath(), screenshot, r)).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getDifference(
            String alphaFramePath,
            BufferedImage betaFrame,
            Response.ResponseBuilder response
    ) throws IOException {
        BufferedImage alphaFrame = ImageIO.read(new File(alphaFramePath));

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
            ImageIO.write(betaFrame, "png", new File(alphaFramePath));
            response.header("Frame-Type", "alpha");
            return betaFrame;
        }

        response.header("Frame-Type", "difference");

        return outputFrame;
    }
}
