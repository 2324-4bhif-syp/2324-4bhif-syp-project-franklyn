package at.htl.franklyn.recorder.boundary;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import javax.imageio.ImageIO;
import javax.xml.datatype.Duration;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/video")
public class VideoResource {

    @ConfigProperty(name = "screenshots.path")
    String screenshotsPath;

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Blocking
    public Uni<Response> downloadAllVideos(){

        ResponseBuilder response = Response.serverError();

        try {
            // Parent-folder
            File screenshotFolder = new File(screenshotsPath);
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles();

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return Uni.createFrom().item(response.build());
            }

            response =  Response
                    .ok()
                    .header(
                            "Content-Disposition",
                            "attachment; filename=\"compressed.zip\""
                    )
                    .entity(
                            returnZipFile(
                                    targetDirectories,
                                    Paths.get(screenshotsPath, "compressed.zip").toString()
                            )
                    );

            return Uni.createFrom().item(response.build());
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
                ZipEntry zipEntry = new ZipEntry(String.format("%s.mp4", fileName));
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

    @GET
    @Path("/download/{username}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Blocking
    public Uni<Response> downloadOneVideo(@PathParam("username") String username){

        ResponseBuilder response = Response.serverError();

        try{
            // Parent-folder
            File screenshotFolder = new File(screenshotsPath);
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles((f, name) -> name.equals(username));

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return Uni.createFrom().item(response.build());
            }

            File targetDirectory = targetDirectories[0];

            FileOutputStream fos = new FileOutputStream(
                    Paths.get(targetDirectory.getPath(), String.format("%s.zip", username)).toString()
            );
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            InputStream fis = getVideo(username);
                    /*.map(video -> video.readEntity(InputStream.class))
                    .await()
                    .indefinitely(); // change to atMost() later

                     */

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

            response = Response
                    .ok()
                    .entity(
                            Paths.get(
                                    targetDirectory.getPath(),
                                    String.format("/%s.zip", username)
                            ).toFile()
                    );

            return Uni.createFrom().item(response.build());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Path("/{username}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @GET
    public FileInputStream getVideo(@PathParam("username") String username) {

        try {
            // Parent-folder
            File screenshotFolder = new File(screenshotsPath);
            // Input/Output-folder
            File[] targetDirectories = screenshotFolder.listFiles((f, name) -> name.equals(username));

            // Return if folder with this user does not exist
            if(targetDirectories == null || targetDirectories.length == 0 ){
                return null;
            }

            File targetDirectory = targetDirectories[0];

            // Get all images
            File[] screenshots = targetDirectory.listFiles(f -> f.getName().endsWith("png"));

            // If there are none return
            if(screenshots == null){
                return null;
            }

            // Make sure Files are in the right order
            Arrays.sort(screenshots);

            // Set path and size for recorder
            FFmpegFrameRecorder recorder = getRecorder(screenshots[0], targetDirectory, username);

            // record images
            record(recorder, screenshots);

            recorder.release();
            recorder.close();

            return new FileInputStream(Paths.get(
                    targetDirectory.getPath(),
                    String.format("%s.mp4", username)
            ).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FFmpegFrameRecorder getRecorder(File sample, File targetDirectory, String ip) throws IOException{
        // Get just one image to set width and height
        BufferedImage test = ImageIO.read(sample);

        // Set path and size for recorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                Paths.get(targetDirectory.getPath(), String.format("%s.mp4", ip)).toString(),
                test.getWidth(),
                test.getHeight()
        );

        // more settings
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setVideoOption("-movflags", "+faststart");
        recorder.setVideoOption("-crf", "40");
        recorder.setVideoOption("-preset", "ultrafast");
        recorder.setVideoOption("-tune", "zerolatency");
        recorder.setVideoOption("threads", "0");
        recorder.setFormat("mp4");
        recorder.setFrameRate(1);
        recorder.setVideoBitrate(600000);

        test.getGraphics().dispose();
        test.flush();

        return recorder;
    }

    private void record(FFmpegFrameRecorder recorder, File[] images) {

        try {
            // Converts Mat to Frame

            recorder.start();

            for (File imageFile : images) {
                // Skip all files that are not png
                if (!imageFile.getName().endsWith("png")) {
                    continue;
                }

                // Convert Mat type to Frame
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(imageFile.getPath());
                grabber.start();
                Frame frame = grabber.grabFrame();
                // Add frame to video
                recorder.record(frame);

                grabber.release();
                grabber.close();
                frame.close();
            }

            recorder.stop();
            recorder.release();
            recorder.close();
        }
        catch (Exception e) {
            Log.error(e.getMessage());
        }
    }
}
