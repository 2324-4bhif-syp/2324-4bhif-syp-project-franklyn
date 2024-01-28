package at.htl.franklyn.boundary;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@Path("/video")
public class VideoResource {

    @Path("/download")
    @Produces("file/zip")
    @GET
    public File downloadAllVideos(){
        try {
            // Parent-folder
            File screenshotFolder = new File("screenshots");
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles();

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return null;
            }

            FileOutputStream fos = new FileOutputStream("screenshots/compressed.zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            List<File> allVideos = new ArrayList<File>();

            for(File files : targetDirectories){
                String[] fileName = files.getName().split("-");

                if(fileName.length != 2){
                    continue;
                }

                File fileToZip = getVideo(fileName[0]);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }

            zipOut.close();
            fos.close();

            return new File("screenshots/compressed.zip");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Path("/download/{ip}")
    @Produces("video/avi")
    @GET
    public File downloadOneVideo(@PathParam("ip") String ip){
        return getVideo(ip);
    }

    @Path("/{ip}")
    @Produces("video/mp4")
    @GET
    public File getVideo(@PathParam("ip") String ip) {

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
            FFmpegFrameRecorder recorder = getRecorder(screenshots[0], targetDirectory, ip);

            // record images
            record(recorder, screenshots);

            return new File(String.format("%s/%s.mp4", targetDirectory.getPath(), ip));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FFmpegFrameRecorder getRecorder(File sample, File targetDirectory, String ip) throws IOException{
        // Get just one image to set width and height
        BufferedImage test = ImageIO.read(sample);

        // Set path and size for recorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                String.format("%s/%s.mp4", targetDirectory.getPath(), ip),
                test.getWidth(),
                test.getHeight()
        );

        // more settings
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
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
