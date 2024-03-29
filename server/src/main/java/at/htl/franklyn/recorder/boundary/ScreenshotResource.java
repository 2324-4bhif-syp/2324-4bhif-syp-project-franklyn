package at.htl.franklyn.recorder.boundary;

import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

@Path("/screenshot")
public class ScreenshotResource {

    @ConfigProperty(name = "timestamp.pattern")
    String timestampPattern;

    @ConfigProperty(name = "screenshots.path")
    String screenshotsPath;

    @POST
    @Path("{username}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void takeScreenshot(@PathParam("username") String username,
                               @RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream screenshot){
        try {
            // TODO: Sanitize username
            File directory = Paths.get(screenshotsPath, username).toFile();

            if(!directory.exists()){
                directory.mkdirs();
            }

            ImageIO.write(
                    ImageIO.read(screenshot),
                    "png",
                    Paths.get(
                            directory.getPath(),
                            String.format("/%s-%s.png",
                                    username,
                                    new SimpleDateFormat(timestampPattern).format(new Date()))
                    ).toFile()
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @GET
    @Path("{username}")
    @Produces("image/png")
    public Response getScreenshot(@PathParam("username") String username){

        // TODO: Sanitize username
        File directory = Paths.get(screenshotsPath, username).toFile();

        Response response = Response.status(404).entity(null).build();

        if(!directory.exists()){
            return response;
        }

        File[] files = directory.listFiles();

        if(files == null){
            return response;
        }

        try {
            File newestScreenshot = Collections.max(
                Arrays.stream(files)
                        .filter(file -> file.getName().endsWith("png"))
                        .toList(),
                Comparator.comparing(File::getName)
            );

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage image = ImageIO.read(newestScreenshot);

            ImageIO.write(image, "png", byteArrayOutputStream);
            response = Response
                    .ok()
                    .entity(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @GET
    @Path("{username}/{width}/{height}")
    @Produces("image/png")
    public Response getScreenshotWithSpecificDimensions(
            @PathParam("username") String username,
            @PathParam("width") int width,
            @PathParam("height") int height)
    {

        Response response = Response.status(404).entity(null).build();

        try{
            BufferedImage screenShot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            ByteArrayInputStream bis = (ByteArrayInputStream) getScreenshot(username).getEntity();

            if(bis == null)
            {
                return response;
            }

            Image scaledImage = ImageIO.read(bis).getScaledInstance(width, height, Image.SCALE_SMOOTH);

            screenShot.getGraphics().drawImage(scaledImage, 0, 0, null);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(screenShot, "png", byteArrayOutputStream);
            response = Response
                    .ok()
                    .entity(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))
                    .build();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }
}
