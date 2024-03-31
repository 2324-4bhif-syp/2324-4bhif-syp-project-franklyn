package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.control.ExamineeRepository;
import at.htl.franklyn.server.services.ScreenshotService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/state")
public class StateResource {
    @Inject
    ExamineeRepository examineeRepository;

    @Inject
    ScreenshotService screenshotService;

    @POST
    @Path("reset")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reset() {
        Response response = Response.ok().build();

        examineeRepository.clear();
        if(!screenshotService.deleteAllScreenshots()) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }
}
