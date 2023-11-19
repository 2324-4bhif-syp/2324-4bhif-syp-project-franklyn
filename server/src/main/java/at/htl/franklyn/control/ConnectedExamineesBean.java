package at.htl.franklyn.control;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class ConnectedExamineesBean {
    @Inject
    ExamineeConnectionRepository examineeConnectionRepository;

    @ConfigProperty(name = "connectedexamineesbean.lifetime")
    long MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS;

    @Scheduled(every = "{connectedexamineesbean.interval}")
    public void removeExpiredExamineeConnections() {
        List<String> expiredExamineIpAddress = examineeConnectionRepository.getAllExpiredExamines(MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS);

        for (String ipAddress : expiredExamineIpAddress) {
            examineeConnectionRepository.removeConnection(ipAddress);
        }

        if (expiredExamineIpAddress.size() == 1) {
            Log.info("Removed " + expiredExamineIpAddress.size() + " connected client.");
        } else {
            Log.info("Removed " + expiredExamineIpAddress.size() + " connected clients.");
        }
    }
}
