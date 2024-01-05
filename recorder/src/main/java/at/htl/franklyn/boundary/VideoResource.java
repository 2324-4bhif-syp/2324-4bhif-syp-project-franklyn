package at.htl.franklyn.boundary;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import nu.pattern.OpenCV;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;

@Path("/video")
public class VideoResource {


    @Path("/{ip}")
    @Produces("video/avi")
    @GET
    public Response getScreenshot(@PathParam("ip") String ip) {
        OpenCV.loadLocally();

        //Log.info(org.opencv.core.Core.getBuildInformation());

        Response.ResponseBuilder response = Response.serverError();

        FilenameFilter filter = (f, name) -> name.startsWith(ip);

        try {
            File screenshotFolder = new File("screenshots");

            File targetDirectory = Objects.requireNonNull(screenshotFolder.listFiles(filter))[0];

            if(targetDirectory == null){
                response = Response.status(Response.Status.BAD_REQUEST);
                return response.build();
            }

            File[] screenshots = targetDirectory.listFiles();

            if(screenshots == null){
                response = Response.status(Response.Status.BAD_REQUEST);
                return response.build();
            }

            Arrays.sort(screenshots);

            Size imageSize = new Size(
                    ImageIO.read(screenshots[0]).getWidth(),
                    ImageIO.read(screenshots[0]).getHeight()
            );

            VideoWriter vw = new VideoWriter();

            vw.open(
                    String.format("%s/video.avi", targetDirectory.getAbsolutePath()),
                    VideoWriter.fourcc('M', 'J', 'P', 'G'),
                    1,
                    imageSize
            );

            if(!vw.isOpened()){
                Log.info("not open????");
                return response.build();
            }

            Mat frame = new Mat(imageSize,  CvType.CV_8UC3);

            for(File screenshot : screenshots){
                if(!screenshot.getName().endsWith(".png")){
                   continue;
                }

                BufferedImage image = ImageIO.read(screenshot);

                byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                frame.put(0, 0, pixels);

                vw.write(frame);
            }

            response = Response.ok();

            vw.release();

            return response.entity(new File(String.format("%s/video.avi", targetDirectory.getPath()))).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
