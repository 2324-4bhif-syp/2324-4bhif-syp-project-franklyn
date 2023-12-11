package at.htl.franklyn.entity;

public class Examinee {
    private String userName;
    private String ipAddress;
    private boolean isConnected;

    public Examinee() {
    }

    public Examinee(String userName, String ipAddress, boolean isConnected) {
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.isConnected = isConnected;
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
}
