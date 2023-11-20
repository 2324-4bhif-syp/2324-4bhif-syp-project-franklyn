package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ExamineesService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static java.util.Objects.requireNonNull;

@Path("/overview")
public class OverviewPage {
    @Inject
    @RestClient
    ExamineesService examineesService;

    private final Template overview;

    public OverviewPage(Template overview) {
        this.overview = requireNonNull(overview, "page is required");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance get() {

        return overview.data("examinees", examineesService.getExaminees());
    }

}
