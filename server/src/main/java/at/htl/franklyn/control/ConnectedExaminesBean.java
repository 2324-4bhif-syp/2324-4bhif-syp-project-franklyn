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

    private final long maxLiveTimeOfConnectionInSeconds = 120;
    private final String schedulerTime = maxLiveTimeOfConnectionInSeconds + "s";

    public long getMaxLiveTimeOfExamineConnections() {
        return maxLiveTimeOfConnectionInSeconds;
    }

    @Scheduled(every = schedulerTime)
    public void removeExpiredExamineConnections() {
        List<String> expiredExamineIpAdress = examineeConnectionRepository.getAllExpiredExamines(maxLiveTimeOfConnectionInSeconds);

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
