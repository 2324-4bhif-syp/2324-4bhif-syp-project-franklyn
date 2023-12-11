package at.htl.franklyn.services;

import at.htl.franklyn.entity.Examinee;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/examinees")
@RegisterRestClient(configKey = "server-address")
public interface ExamineesService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Examinee> getExaminees();
}
