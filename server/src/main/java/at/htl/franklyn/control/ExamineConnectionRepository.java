package at.htl.franklyn.control;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ExamineConnectionRepository {
    // ConcurrentHashmap with the Ip-Adress as the key
    private ConcurrentHashMap<String, LocalDateTime> connectedExamines = new ConcurrentHashMap<>();

    private long connectionTimeInMinutes = 2;

    public ConcurrentHashMap<String, LocalDateTime> getConnectedExamines() {
        return connectedExamines;
    }

    public boolean addConnection(String ipAdress) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        boolean validIpAdress = false;

        if (ipAdress.matches(PATTERN)) {
            validIpAdress = true;
            connectedExamines.put(ipAdress, LocalDateTime.now());
        }

        return validIpAdress;
    }

}
