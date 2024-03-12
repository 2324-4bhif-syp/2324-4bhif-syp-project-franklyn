package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.boundary.Dto.ExamineeDto;
import at.htl.franklyn.server.control.ExamineeRepository;
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
        return Response.ok(examineeRepository.findAll().stream().map(e -> new ExamineeDto(
                e.getUsername(),
                e.isConnected())
        )).build();
    }

    @GET
    @Path("reset")
    @Produces(MediaType.TEXT_PLAIN)
    public Response clearExaminees() {
        this.examineeRepository.clear();
        return Response.ok().build();
    }
}
