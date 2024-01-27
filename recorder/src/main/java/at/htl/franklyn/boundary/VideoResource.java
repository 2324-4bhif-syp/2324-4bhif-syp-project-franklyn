package at.htl.franklyn.boundary;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@Path("/video")
public class VideoResource {


    @Path("/{ip}")
    @Produces("video/mp4")
    @GET
    public File getScreenshot(@PathParam("ip") String ip) {

        try {
            // Parent-folder
            File screenshotFolder = new File("screenshots");
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles((f, name) -> name.startsWith(ip));

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return null;
            }

            File targetDirectory = targetDirectories[0];

            // Get all images
            File[] screenshots = targetDirectory.listFiles();

            // If there aro none return
            if(screenshots == null){
                return null;
            }

            // Make sure Files are in the right order
            Arrays.sort(screenshots);

            // Set path and size for recorder
            FFmpegFrameRecorder recorder = getRecorder(screenshots[0], targetDirectory);

            // record images
            record(recorder, screenshots);

            return new File(String.format("%s/video.mp4", targetDirectory.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FFmpegFrameRecorder getRecorder(File sample, File targetDirectory) throws IOException{
        // Get just one image to set width and height
        BufferedImage test = ImageIO.read(sample);

        // Set path and size for recorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                String.format("%s/video.mp4", targetDirectory.getPath()),
                test.getWidth(),
                test.getHeight()
        );

        // more settings
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setFrameRate(1);
        recorder.setVideoBitrate(600000);

        return recorder;
    }

    private void record(FFmpegFrameRecorder recorder, File[] images) {
        // Converts Mat to Frame
        try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {

            recorder.start();

            for (File imageFile : images) {
                // Skip all files that are not png
                if (!imageFile.getName().endsWith("png")) {
                    continue;
                }

                // Convert Mat type to Frame
                Frame frame = converter.convert(imread(imageFile.getPath()));
                // Add frame to video
                recorder.record(frame);
            }

            recorder.stop();
            recorder.release();
        }
        catch (Exception e) {
            Log.error(e.getMessage());
        }
    }
}
