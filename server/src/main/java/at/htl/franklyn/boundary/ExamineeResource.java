package at.htl.franklyn.boundary;

import at.htl.franklyn.boundary.Dto.ExamineeDto;
import at.htl.franklyn.control.ExamineeRepository;
import at.htl.franklyn.entity.ExamineeState;
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
                e.getIpAddresses(),
                e.getConnectionState() == ExamineeState.CONNECTED)
        )).build();
    }
}
