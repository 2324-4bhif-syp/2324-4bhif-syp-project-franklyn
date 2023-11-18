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
    String MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS;

    @Scheduled(every = "{connectedexamineesbean.interval}")
    public void removeExpiredExamineeConnections() {
        List<String> expiredExamineIpAdress = examineeConnectionRepository.getAllExpiredExamines(Long.parseLong(MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS));

        for (String ipAdress : expiredExamineIpAdress) {
            examineeConnectionRepository.removeConnection(ipAdress);
        }

        if (expiredExamineIpAdress.size() == 1) {
            Log.info("Removed " + expiredExamineIpAdress.size() + " connected client.");
        } else {
            Log.info("Removed " + expiredExamineIpAdress.size() + " connected clients.");
        }
    }
}
