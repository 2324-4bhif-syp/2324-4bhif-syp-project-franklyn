package at.htl.franklyn.control;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConnectedExaminesBean {
    @Inject
    ExamineeConnectionRepository examineeConnectionRepository;

    private static final long MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS = 120;
    private static final String SCHEDULER_TIME = MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS + "s";

    @Scheduled(every = SCHEDULER_TIME)
    public void removeExpiredExamineeConnections() {
        List<String> expiredExamineIpAdress = examineeConnectionRepository.getAllExpiredExamines(MAX_LIVE_TIME_OF_CONNECTION_IN_SECONDS);

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
