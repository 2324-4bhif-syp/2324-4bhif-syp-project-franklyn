package at.htl.franklyn.server.feature.telemetry;

import at.htl.franklyn.server.common.ExceptionFilter;
import at.htl.franklyn.server.feature.examinee.ExamineeRepostiory;
import at.htl.franklyn.server.feature.telemetry.image.FrameType;
import at.htl.franklyn.server.feature.telemetry.image.ImageService;
import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import at.htl.franklyn.server.feature.telemetry.video.*;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.BackPressureStrategy;
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.file.OpenOptions;
import io.vertx.mutiny.core.Vertx;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;

@Path("/telemetry")
public class TelemetryResource {
    @Inject
    ImageService imageService;

    @Inject
    VideoJobRepository videoJobRepository;

    @Inject
    VideoJobService videoJobService;

    @Inject
    ExamineeRepostiory examineeRepostiory;

    @Inject
    Vertx vertx;

    private static MultiEmitter<? super String> emitter;
    private static final Multi<String> stream = Multi.createFrom().emitter(e -> {
        emitter = e;
    }, BackPressureStrategy.BUFFER);

    @POST
    @Path("/by-session/{sessionId}/screen/upload/alpha")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @WithTransaction
    public Uni<Response> saveAlphaFrame(
            @PathParam("sessionId") String sessionId,
            @RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream alphaFrame,
            @RestForm("sus") @PartType(MediaType.TEXT_PLAIN) boolean isTooLong
    ) {
        if (isTooLong){
            Log.info("ZUUUU LANG");
        }

        return Uni.createFrom()
                .item(sessionId)
                .onItem().transform(UUID::fromString)
                .onFailure(ExceptionFilter.NO_WEBAPP).transform(e -> new WebApplicationException(
                        "invalid sessionId / participationId", Response.Status.BAD_REQUEST
                ))
                .chain(session -> imageService.saveFrameOfSession(session, alphaFrame, FrameType.ALPHA, isTooLong))
                .onFailure(ExceptionFilter.NO_WEBAPP).transform(e -> {
                    Log.warnf("Could not save frame of %s (Reason: %s)", sessionId, e.getMessage());
                    return new WebApplicationException(
                            "Unable to save frame", Response.Status.BAD_REQUEST
                    );
                })
                .invoke(() -> {
                    if (isTooLong) {
                        examineeRepostiory.getExamineeWithSessionId(UUID.fromString(sessionId))
                                .onItem()
                                .transform(u -> String.format("%s %s",
                                        u.getFirstname(),
                                        u.getLastname()
                                ))
                                .subscribe().with(name -> emitter.emit(name));
                    }
                })
                .onItem().transform(v -> Response.ok().build());
    }

    @POST
    @Path("/by-session/{sessionId}/screen/upload/beta")
    @WithTransaction
    public Uni<Response> saveBetaFrame(
            @PathParam("sessionId") String sessionId,
            @RestForm("image") @PartType(MediaType.APPLICATION_OCTET_STREAM) InputStream betaFrame,
            @RestForm("sus") @PartType(MediaType.TEXT_PLAIN) boolean isTooLong
    ) {
        if (isTooLong){
            Log.info("ZUUUU LANG");
        }

        return Uni.createFrom()
                .item(sessionId)
                .onItem().transform(UUID::fromString)
                .onFailure(ExceptionFilter.NO_WEBAPP).transform(e -> new WebApplicationException(
                        "invalid sessionId / participationId", Response.Status.BAD_REQUEST
                ))
                .chain(session -> imageService.saveFrameOfSession(session, betaFrame, FrameType.BETA, isTooLong))
                .onFailure(ExceptionFilter.NO_WEBAPP).transform(e -> {
                    Log.warnf("Could not save frame of %s (Reason: %s)", sessionId, e.getMessage());
                    return new WebApplicationException(
                            "Unable to save frame", Response.Status.BAD_REQUEST
                    );
                })
                .invoke(() -> {
                    if (isTooLong) {
                        examineeRepostiory.getExamineeWithSessionId(UUID.fromString(sessionId))
                                .onItem()
                                .transform(u -> String.format("%s %s",
                                        u.getFirstname(),
                                        u.getLastname()
                                ))
                                .subscribe().with(name -> emitter.emit(name));
                    }
                })
                .onItem().transform(v -> Response.ok().build());
    }

    @GET
    @Path("/by-user/{userId}/{examId}/screen/download")
    @Produces("image/png") // Hardcoded since MediaType enum does not have image/png
    @WithSession
    public Uni<Response> downloadFrame(
            @PathParam("userId") Long userId,
            @PathParam("examId") Long examId
    ) {

        return Uni.createFrom()
                .item(userId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing userId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .replaceWith(examId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing examId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .chain(isSus -> imageService.loadLatestFrameOfUser(examId, userId))
                .onItem().transform(buf -> Response.ok(buf).build())
                .onFailure(IllegalStateException.class).transform(e -> new WebApplicationException(
                        "No available screenshot found",
                        Response.Status.NOT_FOUND
                ))
                .onFailure(ExceptionFilter.NO_WEBAPP).transform(e -> {
                    Log.warnf("Could not load screenshot for user: %d | exam: %d", userId, examId);
                    return new WebApplicationException(
                            "Could not load screenshot for user",
                            Response.Status.BAD_REQUEST
                    );
                });
    }

    @GET
    @Path("/jobs/video/{job-id}")
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> getJobStatus(
            @PathParam("job-id") Long jobId
    ) {
        return Uni.createFrom()
                .item(jobId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing jobId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .chain(id -> videoJobRepository.findById(id))
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Job does not exist",
                                Response.Status.NOT_FOUND
                        )
                )
                .onItem().transform(job -> new VideoJobDto(
                                job.getId(),
                                job.getState(),
                                job.getExam().getId(),
                                job.getType() == VideoJobType.SINGLE ? job.getExaminee().getId() : null,
                                job.getCreatedAt(),
                                job.getFinishedAt(),
                                job.getErrorMessage()
                        )
                )
                .onItem().transform(dto -> Response.ok(dto).build());
    }

    @GET
    @Path("/jobs/video/{job-id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @WithSession
    public Uni<Response> downloadVideo(@PathParam("job-id") Long jobId) {
        if (jobId == null) {
            return Uni.createFrom().item(
                    Response.status(Response.Status.BAD_REQUEST).entity("No Job Id provided").build()
            );
        }

        return videoJobRepository.findById(jobId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Job does not exist",
                                Response.Status.NOT_FOUND
                        )
                )
                .onItem().transformToUni(Unchecked.function(job -> {
                    if (job.getState() != VideoJobState.DONE) {
                        throw new WebApplicationException("Job is not done", Response.Status.BAD_REQUEST);
                    }
                    return vertx.fileSystem().open(job.getArtifactPath(), new OpenOptions())
                            .onItem().transform(file -> Response
                                    .ok(file)
                                    .header(
                                            "Content-Disposition",
                                            "attachment; filename=\""
                                                    + Paths.get(job.getArtifactPath()).getFileName()
                                                    + "\""
                                    )
                                    .build());
                }));
    }

    @POST
    @Path("/by-user/{user-id}/{exam-id}/video/generate")
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<Response> generateVideoForUser(
            @PathParam("user-id") Long userId,
            @PathParam("exam-id") Long examId,
            @Context UriInfo uriInfo
    ) {
        return Uni.createFrom()
                .item(userId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing userId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .replaceWith(examId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing examId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .chain(ignored -> videoJobService.queueVideoJob(userId, examId))
                .onFailure(NoSuchElementException.class).transform(e -> {
                    Log.warnf("Could not generate video for (exam: %s, user: %s) (Reason: %s)", examId, userId, e.getMessage());
                    return new WebApplicationException(
                            Response.status(Response.Status.BAD_REQUEST).entity("\"Examinee never participated in Exam\"").build()
                    );
                })
                .onFailure(NoImagesAvailableException.class).transform(e -> {
                    Log.warnf("Could not generate video for (exam: %s, user: %s) (Reason: %s)", examId, userId, e.getMessage());
                    return new WebApplicationException(
                            Response.status(Response.Status.BAD_REQUEST).entity("\"No Images available for examinee\"").build()
                    );
                })
                .onItem().transform(job -> new VideoJobDto(
                                job.getId(),
                                job.getState(),
                                job.getExam().getId(),
                                job.getType() == VideoJobType.SINGLE ? job.getExaminee().getId() : null,
                                job.getCreatedAt(),
                                job.getFinishedAt(),
                                job.getErrorMessage()
                        )
                )
                .onItem().transform(job -> {
                    String location = uriInfo.getBaseUriBuilder()
                            .path(TelemetryResource.class)
                            .path("jobs/video/{job-id}")
                            .build(job.id())
                            .toString();

                    return Response.accepted(job)
                            .header("Location", location)
                            .build();
                });
    }

    @POST
    @Path("/by-exam/{exam-id}/video/generate-all")
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<Response> generateVideosForExam(
            @PathParam("exam-id") Long examId,
            @Context UriInfo uriInfo
    ) {
        return Uni.createFrom()
                .item(examId)
                .onItem().ifNull().failWith(
                        new WebApplicationException(
                                "Missing examId",
                                Response.Status.BAD_REQUEST
                        )
                )
                .chain(ignored -> videoJobService.queueBatchVideoJob(examId))
                .onFailure(NoSuchElementException.class).transform(e -> {
                    Log.warnf("Could not generate video for exam: %s (Reason: %s)", examId, e.getMessage());
                    return new WebApplicationException(
                            "Unable to generate videos", Response.Status.BAD_REQUEST
                    );
                })
                .onItem().transform(job -> new VideoJobDto(
                                job.getId(),
                                job.getState(),
                                job.getExam().getId(),
                                job.getType() == VideoJobType.SINGLE ? job.getExaminee().getId() : null,
                                job.getCreatedAt(),
                                job.getFinishedAt(),
                                job.getErrorMessage()
                        )
                )
                .onItem().transform(job -> {
                    String location = uriInfo.getBaseUriBuilder()
                            .path(TelemetryResource.class)
                            .path("jobs/video/{job-id}")
                            .build(job.id())
                            .toString();

                    return Response.accepted(job)
                            .header("Location", location)
                            .build();
                });
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> sendNotification() {
        Log.info("send notification");
        return stream;
    }
}
