package at.htl.franklyn.server.feature.telemetry;

import at.htl.franklyn.server.feature.telemetry.image.FrameType;
import at.htl.franklyn.server.feature.telemetry.image.ImageService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;
import java.util.UUID;

@Path("/telemetry")
public class TelemetryResource {
    @Inject
    ImageService imageService;

    @POST
    @Path("/by-session/{sessionId}/screen/upload/alpha")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @WithTransaction
    public Uni<Response> saveAlphaFrame(
            @PathParam("sessionId") String sessionId,
            @RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream alphaFrame
    ) {
        return Uni.createFrom()
                .item(sessionId)
                .onItem().transform(UUID::fromString)
                .onFailure().transform(BadRequestException::new)
                .chain(session -> imageService.saveFrameOfSession(session, alphaFrame, FrameType.ALPHA))
                .onFailure().transform(BadRequestException::new)
                .onItem().transform(v -> Response.ok().build());
    }

    @POST
    @Path("/by-session/{sessionId}/screen/upload/beta")
    @WithTransaction
    public Uni<Response> saveBetaFrame(
            @PathParam("sessionId") String sessionId,
            @RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream betaFrame
    ) {
        return Uni.createFrom()
                .item(sessionId)
                .onItem().transform(UUID::fromString)
                .onFailure().transform(BadRequestException::new)
                .chain(session -> imageService.saveFrameOfSession(session, betaFrame, FrameType.BETA))
                .onFailure().transform(BadRequestException::new)
                .onItem().transform(v -> Response.ok().build());
    }
}
