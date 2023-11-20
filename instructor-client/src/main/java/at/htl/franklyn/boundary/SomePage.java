package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ExamineesService;
import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static java.util.Objects.requireNonNull;

@Path("/some-page")
public class SomePage {
    @Inject
    @RestClient
    ExamineesService examineesService;

    private final Template page;

    public SomePage(Template page) {
        this.page = requireNonNull(page, "page is required");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @io.smallrye.common.annotation.Blocking
    public TemplateInstance get(@QueryParam("name") String name) {
        // Example usage of examineesService:

        Log.info(examineesService.getExaminees());
        return page.data("name", examineesService.getExaminees().get(0));
    }

}
