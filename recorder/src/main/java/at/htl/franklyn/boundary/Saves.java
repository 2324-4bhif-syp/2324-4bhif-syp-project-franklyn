package at.htl.franklyn.boundary;

import at.htl.franklyn.entity.Examinee;
import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.ScreenshotService;

import io.quarkus.scheduler.Scheduled;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Path("/saves")
public class Saves {
    @RestClient
    ExamineesService examineesService;
    @RestClient
    ScreenshotService screenshotService;

    @ConfigProperty(name = "timestamp.pattern")
    String timestampPattern;

    @Path("/examinees")
    public Response getExaminees(){
        return Response.ok().entity(examineesService.getExaminees()).build();
    }

    @Scheduled(every = "{update.interval}")
    public void update(){
        List<Examinee> examinees = examineesService.getExaminees();

        for(Examinee examine: examinees){
            if(!examine.isConnected()){
                continue;
            }

            try {
                File directory = new File(String.format("screenshots/%s-%s", examine.getIpAddress(), examine.getUserName()));

                if(!directory.exists()){
                    directory.mkdirs();
                }

                screenshotService = RestClientBuilder.newBuilder()
                        .baseUri(URI.create(String.format("http://%s:8081", examine.getIpAddress())))
                        .build(ScreenshotService.class);

                ByteArrayInputStream bis = new ByteArrayInputStream(screenshotService.getScreenshot());

                ImageIO.write(
                        ImageIO.read(bis),
                        "png",
                        new File(String.format("%s/%s-%s.png", 
                            directory.getPath(), 
                            examine.getIpAddress(), 
                            new SimpleDateFormat(timestampPattern).format(new Date())))
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
