package at.htl.franklyn.services;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Singleton;
import jakarta.websocket.Session;

@Singleton
public class ConnectionService implements Scheduled.SkipPredicate {
    private boolean isConnected = false;
    private Session session;

    public boolean test(ScheduledExecution execution) {
        return this.isConnected;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
