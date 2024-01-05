package at.htl.franklyn.control;

import at.htl.franklyn.entity.Examinee;
import at.htl.franklyn.entity.ExamineeState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ExamineeRepository {
    // Key: Username
    private final ConcurrentHashMap<String, Examinee> examinees = new ConcurrentHashMap<>();

    public void save(Examinee examinee) {
        this.examinees.put(examinee.getUsername(), examinee);
    }

    public List<Examinee> findAll() {
        return new ArrayList<>(this.examinees.values());
    }

    public Examinee findByUsername(String username) {
        return this.examinees.get(username);
    }

    public void disconnect(String username) {
        Examinee e = this.findByUsername(username);
        e.setConnectionState(ExamineeState.DISCONNECTED);
        this.save(e);
    }

    public void connect(String username, String ipAddress, Session session) {
        Examinee e = this.findByUsername(username);

        if (e == null) {
            e = new Examinee();
            e.setUsername(username);
        }

        e.setSession(session);
        e.setConnectionState(ExamineeState.AWAITING_IP);
        e.setLastPingTimestamp(LocalDateTime.now());

        this.save(e);
    }

    public void updateIpAddresses(String username, List<String> ipAddresses) {
        Examinee e = this.findByUsername(username);

        if (e != null) {
            e.setIpAddresses(ipAddresses);
            e.setConnectionState(ExamineeState.CONNECTED);
            this.save(e);
        }
    }

    public void refresh(String username, Session session) {
        Examinee e = this.findByUsername(username);
        e.setSession(session);
        e.setConnectionState(ExamineeState.CONNECTED);
        e.setLastPingTimestamp(LocalDateTime.now());
    }
}
