package at.htl.franklyn.boundary;

import at.htl.franklyn.entity.Examinee;
import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.ScreenshotService;
import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import io.quarkus.scheduler.Scheduled;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Path("/saves")
public class Saves {

    @RestClient
    ExamineesService examineesService;
    @RestClient
    ScreenshotService screenshotService;

    private final Template saves;
    private String ip;

    private int counter = 0;

    public Saves(Template saves) {
        this.saves = requireNonNull(saves, "page is required");
    }

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

                screenshotService = RestClientBuilder.newBuilder()
                        .baseUri(URI.create(String.format("http://%s:8081", examine.getIpAddress())))
                        .build(ScreenshotService.class);

                ByteArrayInputStream bis = new ByteArrayInputStream(screenshotService.getScreenshot());

                ImageIO.write(
                        ImageIO.read(bis),
                        "png",
                        new File(String.format("%s.%d.png", examine.getIpAddress(), counter))
                );

                counter++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
