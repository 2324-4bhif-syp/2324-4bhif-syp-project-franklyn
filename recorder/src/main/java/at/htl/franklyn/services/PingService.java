package at.htl.franklyn.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.concurrent.CompletionStage;

@Path("/screenshot")
@RegisterRestClient()
public interface PingService {
    @GET
    @Path("/health")
    CompletionStage<Response> sendPing();
}
