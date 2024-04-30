package at.htl.franklyn.server.entity;

import jakarta.websocket.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryExaminee {
    private String username;
    private List<String> ipAddresses;
    private boolean isConnected;
    private LocalDateTime lastPingTimestamp;
    private Session session;

    public InMemoryExaminee() {
        this.ipAddresses = new ArrayList<>();
    }

    public InMemoryExaminee(String userName,
                            boolean isConnected,
                            LocalDateTime lastPingTimestamp,
                            Session session) {
        this();
        this.username = userName;
        this.isConnected = isConnected;
        this.lastPingTimestamp = lastPingTimestamp;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connectionState) {
        this.isConnected = connectionState;
    }

    public LocalDateTime getLastPingTimestamp() {
        return lastPingTimestamp;
    }

    public void setLastPingTimestamp(LocalDateTime lastPingTimestamp) {
        this.lastPingTimestamp = lastPingTimestamp;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
