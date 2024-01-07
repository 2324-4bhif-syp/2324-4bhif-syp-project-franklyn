package at.htl.franklyn.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public class Examinee {
    @JsonProperty("username")
    private String username;
    @JsonProperty("ipAddresses")
    private String[] ipAddresses;
    @JsonProperty("connected")
    private boolean isConnected;

    public Examinee() {
    }

    public Examinee(String username, String[] ipAddresses, boolean isConnected) {
        this.username = username;
        this.ipAddresses = ipAddresses;
        this.isConnected = isConnected;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String[] getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(String[] ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Examinee examinee = (Examinee) o;
        return isConnected == examinee.isConnected && Objects.equals(username, examinee.username) && Arrays.equals(ipAddresses, examinee.ipAddresses);
    }
}
