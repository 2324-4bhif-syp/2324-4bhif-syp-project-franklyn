package at.htl.franklyn.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

@Entity
@Table(name = "F_CONNECTION_STATE")
public class ConnectionState {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "CS_ID")
    private Long id;

    @NotNull(message = "Ping timestamp can not be null")
    @PastOrPresent(message = "Can not persist ping captured in the future")
    @Column(name = "CS_PING_TIMESTAMP", nullable = false)
    private LocalDateTime pingTimestamp;

    @NotNull(message = "Participation can not be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "CS_PARTICIPATION_ID", nullable = false)
    private Participation participation;

    @Column(name = "CS_IS_CONNECTED", nullable = false)
    private boolean isConnected;

    public ConnectionState() {
    }

    public ConnectionState(LocalDateTime pingTimestamp, Participation participation, boolean isConnected) {
        this.pingTimestamp = pingTimestamp;
        this.participation = participation;
        this.isConnected = isConnected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPingTimestamp() {
        return pingTimestamp;
    }

    public void setPingTimestamp(LocalDateTime pingTimestamp) {
        this.pingTimestamp = pingTimestamp;
    }

    public Participation getParticipation() {
        return participation;
    }

    public void setParticipation(Participation participation) {
        this.participation = participation;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public String toString() {
        return "ConnectionState{" +
                "id=" + id +
                ", pingTimestamp=" + pingTimestamp +
                ", participation=" + participation +
                ", isConnected=" + isConnected +
                '}';
    }
}
