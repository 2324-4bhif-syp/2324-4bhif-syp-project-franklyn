package at.htl.franklyn.boundary;

import at.htl.franklyn.control.ExamineConnectionRepository;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/connection")
public class ExamineConnectionResource {
    @Inject
    ExamineConnectionRepository examineConnectionRepository;

    private final String jsonIpObjectName = "ip-adress";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response connect(JsonObject connect) {
        Response response;

        if (connect.containsKey(jsonIpObjectName)) {
            if (examineConnectionRepository.addConnection(connect.getString(jsonIpObjectName))) {
                response = Response.accepted().build();
            } else {
                response = Response
                        .status(Response.Status.NOT_ACCEPTABLE)
                        .header("CONNECTION-NOT-ACCEPTED", "Please send a valid IPv4-Adress.")
                        .build();
            }
        } else {
            response = Response
                    .status(Response.Status.BAD_REQUEST)
                    .header("CONNECTION-NOT-ACCEPTED", "Please send a valid json body.")
                    .build();
        }

        return response;
    }

}
