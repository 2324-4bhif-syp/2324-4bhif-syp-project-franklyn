package at.htl.franklyn.entity;

import com.aayushatharva.brotli4j.common.annotations.Local;
import jakarta.websocket.Session;

import java.time.LocalDateTime;

public class Examinee {
    private String userName;
    private String ipAddress;
    private boolean isConnected;
    private LocalDateTime lastPingTimestamp;
    private Session session;

    public Examinee() {
    }

    public Examinee(String userName,
                    String ipAddress,
                    boolean isConnected,
                    LocalDateTime lastPingTimestamp,
                    Session session) {
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.isConnected = isConnected;
        this.lastPingTimestamp = lastPingTimestamp;
        this.session = session;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
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
