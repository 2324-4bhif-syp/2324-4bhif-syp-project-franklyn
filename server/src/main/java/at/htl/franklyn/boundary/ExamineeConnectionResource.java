package at.htl.franklyn.boundary;

import at.htl.franklyn.control.ExamineeConnectionRepository;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/connection")
public class ExamineeConnectionResource {
    @Inject
    ExamineeConnectionRepository examineeConnectionRepository;

    @Inject
    RoutingContext context;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response connect() {
        Response response;

        if (examineeConnectionRepository.addConnection(context.request().remoteAddress().host())) {
            response = Response.noContent().build();
        } else {
            response = Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .header("CONNECTION-NOT-ACCEPTED", "Please send a valid IPv4-Adress.")
                    .build();
        }

        return response;
    }

}
