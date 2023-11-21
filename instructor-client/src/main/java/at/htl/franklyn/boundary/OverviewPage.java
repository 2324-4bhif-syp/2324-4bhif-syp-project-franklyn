package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.ScreenshotService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

@Path("/overview")
public class OverviewPage {
    @RestClient
    ExamineesService examineesService;
    @RestClient
    ScreenshotService screenshotService;

    private final Template overview;
    private String ip;

    public OverviewPage(Template overview) {
        this.overview = requireNonNull(overview, "page is required");
    }

    @GET
    @Path("screenshot/{ip}")
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance updateScreenshot(@PathParam("ip") String ip) {
        if ((this.ip == null || !this.ip.equals(ip)) || screenshotService == null) {
            screenshotService = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(String.format("http://%s:8080", ip)))
                    .build(ScreenshotService.class);
            this.ip = ip;
        }

        return this.overview.data("examinees", examineesService.getExaminees())
                .data("image", Base64.getEncoder().encodeToString(screenshotService.getScreenshot()));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance get() {
        return overview.data("examinees", examineesService.getExaminees())
                .data("image", "");
    }
}
