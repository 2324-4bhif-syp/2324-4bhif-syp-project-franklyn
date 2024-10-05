package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.control.ExamRepository;
import at.htl.franklyn.server.control.ParticipationRepository;
import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.entity.Participation;
import at.htl.franklyn.server.entity.dto.ExamDto;
import at.htl.franklyn.server.entity.dto.ExamineeDto;
import at.htl.franklyn.server.services.ExamService;
import at.htl.franklyn.server.services.ExamineeService;
import at.htl.franklyn.server.services.ParticipationService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
    ParticipationRepository participationRepository;

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
        return examRepository.deleteById(id)
                .onItem().transform(deleted -> {
                    if (deleted) {
                        return Response
                                .noContent()
                                .build();
                    } else {
                        return Response
                                .status(Response.Status.NOT_FOUND)
                                .build();
                    }
                });
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
    @Path("/start/{id}")
    public Uni<Response> startExam(@PathParam("id") long id) {
        return null;
    }

    @POST
    @WithTransaction
    @Path("/complete/{id}")
    public Uni<Response> completeExam(@PathParam("id") long id) {
        return null;
    }

    @POST
    @WithTransaction
    @Path("/deleteTelemetry/{id}")
    public Uni<Response> deleteTelemetryOfExam(@PathParam("id") long id) {
        return null;
    }
}
