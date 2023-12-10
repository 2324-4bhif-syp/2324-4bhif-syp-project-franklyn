package at.htl.franklyn.boundary;

import at.htl.franklyn.control.ExamineeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/examinees")
public class ExamineeResource {
    @Inject
    ExamineeRepository examineeRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExaminees() {
        return Response.ok(examineeRepository.findAll()).build();
    }
}
