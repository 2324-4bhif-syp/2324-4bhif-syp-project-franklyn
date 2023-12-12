package at.htl.franklyn.control;

import at.htl.franklyn.entity.Examinee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExamineeRepository {
    // Key: Ip address
    private final ConcurrentHashMap<String, Examinee> examinees = new ConcurrentHashMap<>();

    public void save(Examinee examinee) {
        this.examinees.put(examinee.getIpAddress(), examinee);
    }

    public List<Examinee> findAll() {
        return new ArrayList<>(this.examinees.values());
    }

    public Examinee findByIpAddress(String ipAddress) {
        return this.examinees.get(ipAddress);
    }

    public void disconnect(String ipAddress) {
        Examinee e = this.findByIpAddress(ipAddress);
        e.setConnected(false);
        this.save(e);
    }

    public void connect(String userName, String ipAddress, Session session) {
        Examinee e = this.findByIpAddress(ipAddress);

        if (e == null) {
            e = new Examinee();
            e.setIpAddress(ipAddress);
        }

        // Also update userName in case it changed
        // (examinees switched computer or restarted franklyn and entered a different userName)
        e.setUserName(userName);
        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());

        this.save(e);
    }

    public void refresh(String ipAddress, Session session) {
        Examinee e = this.findByIpAddress(ipAddress);
        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());
    }
}
