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
    @Transactional
    public Response createExam(@Valid ExamDto examDto, @Context UriInfo uriInfo) {
        Exam exam = examService.createExam(examDto);

        URI uri = uriInfo
                .getAbsolutePathBuilder()
                .path(exam.getId().toString())
                .build();

        return Response
                .created(uri)
                .entity(exam)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getExamById(@PathParam("id") long id) {
        if (!examService.exists(id)) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .ok(examRepository.findById(id))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllExams() {
        return Response
                .ok(examRepository.listAll())
                .build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteExamById(@PathParam("id") long id) {
        if (!examService.exists(id)) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        examRepository.deleteById(id);

        return Response
                .noContent()
                .build();
    }

    @GET
    @Path("{id}/examinees")
    public Response getExamineesOfExam(@PathParam("id") long id) {
        if (!examService.exists(id)) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response
                .ok(examService.getExamineesOfExam(id))
                .build();
    }

    @POST
    @Transactional
    @Path("/join/{pin}")
    public Response joinExam(@PathParam("pin") int pin, @Valid ExamineeDto examineeDto, @Context UriInfo uriInfo) {
        if(!examService.isValidPIN(pin) || examineeDto == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }

        Examinee examinee = examineeService.getOrCreateExaminee(examineeDto.firstname(), examineeDto.lastname());
        Exam exam = examService.findByPIN(pin);
        Participation participation = participationService.getOrCreateParticipation(examinee, exam);

        // TODO: really think through new location
        URI uri = uriInfo
                .getBaseUriBuilder()
                .path("/connect/")
                .path(participation.getId().toString())
                .build();

        return Response
                .created(uri)
                .build();
    }
}
