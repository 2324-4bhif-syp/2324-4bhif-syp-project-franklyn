package at.htl.franklyn.services;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/connection")
@RegisterRestClient(configKey = "heartbeat-api")
public interface HeartbeatService {
    @POST
    void sendHeartbeat();
}
