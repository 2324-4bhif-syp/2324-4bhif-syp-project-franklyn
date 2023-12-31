package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ScreenshotService;
import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Path("/screenshot")
public class ScreenshotResource {
    /**
     * This endpoint is used by the instructor client to check the different ip addresses
     * @return an empty ok response
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/health")
    @GET
    public Response getHealthCheck() {
        return Response.ok().build();
    }

    @Produces("image/png")
    @GET
    public Response getScreenshot() {
        ResponseBuilder response = Response.serverError();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final BufferedImage image = ScreenshotService.getScreenshot();

            ImageIO.write(image, "png", byteArrayOutputStream);
            response = Response.ok(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        } catch (Exception e) {
            Log.error("FAILED: to write image", e);
        }

        return response.build();
    }


    @Produces("image/png")
    @Path("/{width}/{height}")
    @GET
    public Response getScaledScreenshot(@PathParam("width") int width, @PathParam("height") int height) {
        ResponseBuilder response = Response.serverError();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final BufferedImage image = ScreenshotService.getScreenshot(width, height);

            ImageIO.write(image, "png", byteArrayOutputStream);
            response = Response.ok(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        } catch (Exception e) {
            Log.error("FAILED: to write image", e);
        }

        return response.build();
    }
}
