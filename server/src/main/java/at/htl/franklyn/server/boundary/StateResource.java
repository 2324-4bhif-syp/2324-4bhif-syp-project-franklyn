package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.boundary.Dto.ServerMetricsDto;
import at.htl.franklyn.server.control.ExamineeRepository;
import at.htl.franklyn.server.services.MetricsService;
import at.htl.franklyn.server.services.ScreenshotService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/state")
public class StateResource {
    @Inject
    ExamineeRepository examineeRepository;

    @Inject
    ScreenshotService screenshotService;

    @Inject
    MetricsService metricsService;

    @POST
    @Path("reset")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reset() {
        Response response = Response.ok().build();

        examineeRepository.clear();
        if(!screenshotService.deleteAllScreenshots()) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    @GET
    @Path("system-metrics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemMetrics() {
        ServerMetricsDto serverMetricsDto = new ServerMetricsDto(
                metricsService.getSystemCpuUsagePercentage(),
                metricsService.getTotalDiskSpaceInBytes(),
                metricsService.getFreeDiskSpaceInBytes(),
                metricsService.getScreenshotsFolderSizeInBytes(),
                metricsService.getTotalMemoryInBytes(),
                metricsService.getUsedMemoryInBytes()
        );

        return Response.ok(serverMetricsDto).build();
    }
}
