package at.htl.franklyn.recorder.boundary;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;

@Path("/video")
public class VideoResource {

    @Path("/download")
    @Produces("file/zip")
    @GET
    public Response downloadAllVideos(){
        try {
            // Parent-folder
            File screenshotFolder = new File("screenshots");
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles();

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return null;
            }

            return Response
                    .ok()
                    .header(
                            "Content-Disposition",
                            "attachment; filename=\"compressed.zip\""
                    )
                    .entity(returnZipFile(targetDirectories, "screenshots/compressed.zip"))
                    .build();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File returnZipFile(File[] targetDirectories, String zipPath){

        try {
            FileOutputStream fos = new FileOutputStream(zipPath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            for(File files : targetDirectories){
                String fileName = files.getName();

                if(fileName.endsWith("zip")){
                    continue;
                }

                InputStream fis = getVideo(fileName);
                ZipEntry zipEntry = new ZipEntry(fileName);
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

            return new File(zipPath);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Path("/download/{username}")
    @Produces("file/zip")
    @GET
    public Response downloadOneVideo(@PathParam("username") String username){
        try{
            // Parent-folder
            File screenshotFolder = new File("screenshots");
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles((f, name) -> name.equals(username));

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return null;
            }

            File targetDirectory = targetDirectories[0];

            FileOutputStream fos = new FileOutputStream(String.format("%s/%s.zip", targetDirectory.getPath(), username));
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            InputStream fis = getVideo(username);
            ZipEntry zipEntry = new ZipEntry(String.format("%s.mp4", username));
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

            zipOut.close();
            fis.close();
            fos.close();

            return Response
                    .ok()
                    .header(
                            "Content-Disposition",
                            String.format("attachment; filename=\"%s.zip\"", username)
                    )
                    .entity(new File(String.format("%s/%s.zip", targetDirectory.getPath(), username)))
                    .build();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Path("/{username}")
    @Produces("video/mp4")
    @GET
    public InputStream getVideo(@PathParam("username") String username) {
        try {
            // Parent-folder
            File screenshotFolder = new File("screenshots");
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles((f, name) -> name.equals(username));

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
            FFmpegFrameRecorder recorder = getRecorder(screenshots[0], targetDirectory, username);

            // record images
            record(recorder, screenshots);

            return new FileInputStream(String.format("%s/%s.mp4", targetDirectory.getPath(), username));
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
        recorder.setVideoOption("-movflags", "+faststart");
        recorder.setVideoOption("-crf", "51");
        recorder.setVideoOption("-preset", "ultrafast");
        recorder.setVideoOption("-tune", "zerolatency");
        recorder.setVideoOption("threads", "0");
        recorder.setFormat("mp4");
        recorder.setFrameRate(1);
        recorder.setVideoBitrate(100000);

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
                Frame frame = converter.convert(opencv_imgcodecs.imread(imageFile.getPath()));
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
