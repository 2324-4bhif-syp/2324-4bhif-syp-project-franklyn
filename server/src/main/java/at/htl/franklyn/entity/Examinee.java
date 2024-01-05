package at.htl.franklyn.entity;

import jakarta.websocket.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Examinee {
    private String username;
    private List<String> ipAddresses;
    private ExamineeState connectionState;
    private LocalDateTime lastPingTimestamp;
    private Session session;

    public Examinee() {
        this.ipAddresses = new ArrayList<>();
    }

    public Examinee(String userName,
                    ExamineeState connectionState,
                    LocalDateTime lastPingTimestamp,
                    Session session) {
        this();
        this.username = userName;
        this.connectionState = connectionState;
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

    public ExamineeState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ExamineeState connectionState) {
        this.connectionState = connectionState;
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
