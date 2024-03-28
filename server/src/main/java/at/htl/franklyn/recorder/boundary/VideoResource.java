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
import org.bytedeco.javacpp.PointerScope;
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

            String inputPath = String.format(
                    "%s/%s-*.png",
                    targetDirectory.getPath(), username
            );

            String outputPath = String.format(
                    "%s/%s.mp4",
                    targetDirectory.getPath(), username
            );

            Runtime r = Runtime.getRuntime();

            Process p = r.exec(
                    String.format(
                            "ffmpeg -y -framerate 1 -pattern_type glob -i %s -c:v libx264 %s",
                            inputPath, outputPath)
            );

            p.waitFor();
            p.destroy();

            r.freeMemory();
            r.gc();

            return new FileInputStream(Paths.get(
                    targetDirectory.getPath(),
                    String.format("%s.mp4", username)
            ).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
