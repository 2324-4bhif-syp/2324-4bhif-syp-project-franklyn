package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.dto.ExamDto;
import at.htl.franklyn.server.services.ExamService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/exams")
public class ExamResource {
    @Inject
    ExamService examService;

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
}
