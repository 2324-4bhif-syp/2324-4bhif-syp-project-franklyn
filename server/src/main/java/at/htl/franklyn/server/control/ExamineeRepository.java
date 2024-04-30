package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.InMemoryExaminee;
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
    private final ConcurrentHashMap<String, InMemoryExaminee> examinees = new ConcurrentHashMap<>();

    public void save(InMemoryExaminee examinee) {
        this.examinees.put(examinee.getUsername(), examinee);
    }

    public void clear() {
        this.examinees.clear();
    }

    public List<InMemoryExaminee> findAll() {
        return new ArrayList<>(this.examinees.values());
    }

    public InMemoryExaminee findByUsername(String username) {
        return this.examinees.get(username);
    }

    public void disconnect(String username) {
        InMemoryExaminee e = this.findByUsername(username);

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
        InMemoryExaminee e = this.findByUsername(username);

        if (e == null) {
            e = new InMemoryExaminee();
            e.setUsername(username);
        }

        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());

        this.save(e);
    }

    public void refresh(String username, Session session) {
        InMemoryExaminee e = this.findByUsername(username);

        if(e == null) {
            Log.warnf("Could not refresh session with %s! User was never fully connected!", username);
            return;
        }

        e.setSession(session);
        e.setConnected(true);
        e.setLastPingTimestamp(LocalDateTime.now());
    }
}
