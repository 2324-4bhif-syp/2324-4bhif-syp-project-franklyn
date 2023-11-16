package at.htl.franklyn.boundary;

import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Path("/connection")
public class ExamineConnectionResource {
    // ConcurrentHashmap with the Ip-Adress as the key
    private ConcurrentHashMap<String, LocalDateTime> connectedExamines = new ConcurrentHashMap<>();
    private final String jsonIpObjectName = "ip-adress";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response connect(JsonObject connect) {
        Response response;
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        if (connect.containsKey(jsonIpObjectName)) {
            if (connect.getString(jsonIpObjectName).matches(PATTERN)) {
                String ipAdress = connect.getString(jsonIpObjectName);

                // inserts the key or updates the connection (last connected time) of the key
                connectedExamines.put(ipAdress, LocalDateTime.now());
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
