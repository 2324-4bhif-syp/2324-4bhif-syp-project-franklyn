package at.htl.franklyn.boundary;

import at.htl.franklyn.control.ExamineeConnectionRepository;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/connection")
public class ExamineeConnectionResource {
    @Inject
    ExamineeConnectionRepository examineeConnectionRepository;

    @Inject
    RoutingContext context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnections() {
        return Response.ok(examineeConnectionRepository.getConnectedExamines()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response connect() {
        Response response;

        if (examineeConnectionRepository.addConnection(context.request().remoteAddress().host())) {
            response = Response.noContent().build();
        } else {
            response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        return response;
    }

}
