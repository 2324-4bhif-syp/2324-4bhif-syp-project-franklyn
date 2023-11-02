package at.htl.franklyn.boundary;

import at.htl.franklyn.Services.ScreenshotService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Path("/screenshot")
public class ScreenshotResource {

    @Produces("image/png")
    @GET
    public Response getScreenshot() {
        BufferedImage image = ScreenshotService.getScreenshot();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            return Response.serverError().build();
        }

        return Response.ok(new ByteArrayInputStream(baos.toByteArray())).build();
    }
}
