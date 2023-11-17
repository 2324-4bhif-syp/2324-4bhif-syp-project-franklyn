package at.htl.franklyn.control;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ExamineeConnectionRepository {
    // ConcurrentHashmap with the Ip-Adress as the key
    private ConcurrentHashMap<String, LocalDateTime> connectedExamines = new ConcurrentHashMap<>();

    public ArrayList<String> getConnectedExamines() {
        return Collections.list(connectedExamines.keys());
    }

    public List<String> getAllExpiredExamines(long addTimeInSeconds) {
        List<String> ipAdresses = new ArrayList<>();

        for (String ipAdress : connectedExamines.keySet()) {
            LocalDateTime maxSurvivalTime = connectedExamines
                    .get(ipAdress)
                    .plusSeconds(addTimeInSeconds);

            if (LocalDateTime.now().isAfter(maxSurvivalTime)) {
                ipAdresses.add(ipAdress);
            }
        }

        return ipAdresses;
    }

    public boolean addConnection(String ipAdress) {
        final String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        boolean validIpAdress = false;

        if (ipAdress.matches(PATTERN)) {
            validIpAdress = true;
            connectedExamines.put(ipAdress, LocalDateTime.now());
        }

        return validIpAdress;
    }

    public void removeConnection(String ipAdress) {
        connectedExamines.remove(ipAdress);
    }
}
