package at.htl.franklyn.server.feature.exam;

import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import at.htl.franklyn.server.feature.examinee.ExamineeDto;
import at.htl.franklyn.server.feature.examinee.ExamineeService;
import at.htl.franklyn.server.feature.telemetry.participation.ParticipationService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/exams")
public class ExamResource {
    @Inject
    ExamService examService;

    @Inject
    ExamineeService examineeService;

    @Inject
    ExamRepository examRepository;

    @Inject
    ParticipationService participationService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<Response> createExam(@Valid ExamDto examDto, @Context UriInfo uriInfo) {
        return examService.createExam(examDto)
                .onItem().transform(e -> {
                    URI uri = uriInfo
                            .getAbsolutePathBuilder()
                            .path(e.getId().toString())
                            .build();

                    return Response
                            .created(uri)
                            .entity(e)
                            .build();
                });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @WithSession
    public Uni<Response> getExamById(@PathParam("id") long id) {
        return examService.exists(id)
                .chain(exists -> {
                    if (!exists) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build());
                    } else {
                        return examRepository.findById(id)
                                .onItem().transform(exam -> Response.ok(exam).build());
                    }
                });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @WithSession
    public Uni<Response> getAllExams() {
        return examRepository.listAll()
                .onItem().transform(exams -> Response.ok(exams).build());
    }

    @DELETE
    @Path("{id}")
    @WithTransaction
    public Uni<Response> deleteExamById(@PathParam("id") long id) {
        return examRepository
                .findById(id)
                .onItem().ifNull().failWith(NotFoundException::new)
                .onFailure().transform(e -> e) // TODO does this work for mapping NotFoundException early before rethrowing BR? // TODO does this work for mapping NotFoundException early before rethrowing BR?
                .onItem().transformToUni(e -> examService.deleteTelemetry(e).replaceWith(e))
                .onItem().transformToUni(e -> examRepository.deleteById(id))
                .onFailure().transform(e -> {
                    Log.error(e);
                    e.printStackTrace();
                    return new BadRequestException(e);
                })
                .onItem().transform(v -> Response.noContent().build());
    }

    @GET
    @Path("{id}/examinees")
    @WithSession
    public Uni<Response> getExamineesOfExam(@PathParam("id") long id) {
        return examService.exists(id)
                .chain(e -> {
                    if (e) {
                        return examService.getExamineesOfExam(id)
                                .onItem().transform(exam -> Response.ok(exam).build());
                    } else {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build());
                    }
                });
    }

    @POST
    @WithTransaction
    @Path("/join/{pin}")
    public Uni<Response> joinExam(@PathParam("pin") int pin, @Valid ExamineeDto examineeDto, @Context UriInfo uriInfo) {
        if (examineeDto == null) {
            return Uni.createFrom().item(
                    Response
                            .status(Response.Status.BAD_REQUEST)
                            .build()
            );
        }

        return examService.isValidPIN(pin)
                .chain(valid -> {
                    if (valid) {
                        return examineeService.getOrCreateExaminee(examineeDto.firstname(), examineeDto.lastname())
                                .chain(examinee -> examService.findByPIN(pin)
                                        .chain(exam -> participationService.getOrCreateParticipation(examinee, exam))
                                )
                                .onItem().transform(p -> {
                                    URI uri = uriInfo
                                            .getBaseUriBuilder()
                                            .path("/connect/")
                                            .path(p.getId().toString())
                                            .build();

                                    return Response
                                            .created(uri)
                                            .build();
                                });
                    } else {
                        return Uni.createFrom().item(
                                Response
                                        .status(Response.Status.BAD_REQUEST)
                                        .build()
                        );
                    }
                });
    }

    @POST
    @WithTransaction
    @Path("/{id}/start")
    public Uni<Response> startExam(@PathParam("id") long id) {
        return examService.exists(id)
                .chain(exists -> {
                    if (!exists) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build());
                    } else {
                        return examRepository.findById(id)
                                .chain(e -> examService.startExam(e))
                                .onFailure().transform(BadRequestException::new)
                                .onItem().transform(t -> Response.ok().build());
                    }
                });
    }

    @POST
    @WithTransaction
    @Path("/{id}/complete")
    public Uni<Response> completeExam(@PathParam("id") long id) {
        return examService.exists(id)
                .chain(exists -> {
                    if (!exists) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build());
                    } else {
                        return examRepository.findById(id)
                                .chain(e -> examService.completeExam(e))
                                .onFailure().transform(BadRequestException::new)
                                .onItem()
                                .transform(x -> Response.ok().build());
                    }
                });
    }

    @DELETE
    @WithTransaction
    @Path("/{id}/telemetry")
    public Uni<Response> deleteTelemetryOfExam(@PathParam("id") long id) {
        return examRepository
                .findById(id)
                .onItem().ifNull().failWith(NotFoundException::new)
                .chain(e -> examService.deleteTelemetry(e))
                .onFailure().transform(BadRequestException::new)
                .onItem()
                .transform(v -> Response.noContent().build());
    }
}
