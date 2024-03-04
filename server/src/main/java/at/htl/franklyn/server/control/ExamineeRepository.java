package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Examinee;
import io.quarkus.logging.Log;
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

        // Could happen when onClose is called before onOpen
        // (sometimes the case when in dev mode and a "hot reload" occurs)
        if(e == null) {
            Log.warnf("Could not disconnect %s! User was never connected!", username);
            return;
        }

        e.setConnected(false);
        this.save(e);
    }

    public void connect(String username, Session session) {
        Examinee e = this.findByUsername(username);

        if (e == null) {
            e = new Examinee();
            e.setUsername(username);
        }

        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());

        this.save(e);
    }

    public void refresh(String username, Session session) {
        Examinee e = this.findByUsername(username);

        if(e == null) {
            Log.warnf("Could not refresh session with %s! User was never fully connected!", username);
            return;
        }

        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());
    }
}
