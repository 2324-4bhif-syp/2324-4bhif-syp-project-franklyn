package at.htl.franklyn.control;

import at.htl.franklyn.services.HeartbeatService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class HeartbeatBean {
    @RestClient
    HeartbeatService heartbeatService;

    @Scheduled(every = "{heartbeat.interval}")
    void sendBeat() {
        Log.info("Sending heartbeat");
        heartbeatService.sendHeartbeat();
    }
}
