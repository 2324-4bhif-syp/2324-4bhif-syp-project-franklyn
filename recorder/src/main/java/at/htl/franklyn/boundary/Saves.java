package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.ScreenshotService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import io.quarkus.scheduler.Scheduled;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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

    @GET
    @Path("screenshot/{ip}")
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance updateScreenshot(@PathParam("ip") String ip) {

        if ((this.ip == null || !this.ip.equals(ip)) || screenshotService == null) {
            screenshotService = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(String.format("http://%s:8081", ip)))
                    .build(ScreenshotService.class);
            this.ip = ip;
        }

        return this.saves.data("examinees", examineesService.getExaminees())
                .data("image", Base64.getEncoder().encodeToString(screenshotService.getScreenshot()));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance get() {
        return saves.data("examinees", examineesService.getExaminees())
                .data("image", "");
    }

    @Scheduled(every = "{update.interval}")
    public void update(){

        List<String> examinees = examineesService.getExaminees();

        for(String examine: examinees){
            try {

                screenshotService = RestClientBuilder.newBuilder()
                        .baseUri(URI.create(String.format("http://%s:8081", examine)))
                        .build(ScreenshotService.class);

                ByteArrayInputStream bis = new ByteArrayInputStream(screenshotService.getScreenshot());

                ImageIO.write(
                        ImageIO.read(bis),
                        "png",
                        new File(String.format("%s.png", examine + counter))
                );

                counter++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
