package at.htl.franklyn.services;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/screenshot")
@RegisterRestClient(baseUri = "")
public interface ScreenshotService {
    @GET
    @Produces("image/png")
    byte[] getScreenshot();
}
