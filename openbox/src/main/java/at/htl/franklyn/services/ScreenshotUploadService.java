package at.htl.franklyn.services;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;

@RegisterRestClient
public interface ScreenshotUploadService {
    @POST
    @Path("alpha")
    Response uploadAlphaScreenshot(@RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream image);

    @POST
    @Path("beta")
    Response uploadBetaScreenshot(@RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream image);
}
