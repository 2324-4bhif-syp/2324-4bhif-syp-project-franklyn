package at.htl.franklyn.server.entity;

import at.htl.franklyn.server.control.Limits;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "F_IMAGE")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "I_ID")
    private Long id;

    @NotNull(message = "Image capture timestamp can not be null")
    @PastOrPresent(message = "Can not persist image captured in the future")
    @Column(name = "I_CAPTURE_TIMESTAMP", nullable = false)
    private LocalDateTime captureTimestamp;

    @NotNull(message = "Participation can not be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "I_PARTICIPATION_ID", nullable = false)
    private Participation participation;

    @NotBlank(message = "Path to image can not be blank")
    @Size(
            message = "Image path must have a length between "
                    + Limits.IMAGE_PATH_LENGTH_MIN + " and "
                    + Limits.IMAGE_PATH_LENGTH_MAX + " characters",
            min = Limits.IMAGE_PATH_LENGTH_MIN,
            max = Limits.IMAGE_PATH_LENGTH_MAX
    )
    @Column(name = "I_PATH", nullable = true, length = Limits.IMAGE_PATH_LENGTH_MAX)
    private String path;

    public Image() {
    }

    public Image(LocalDateTime captureTimestamp, Participation participation, String path) {
        this.captureTimestamp = captureTimestamp;
        this.participation = participation;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCaptureTimestamp() {
        return captureTimestamp;
    }

    public void setCaptureTimestamp(LocalDateTime captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public Participation getParticipation() {
        return participation;
    }

    public void setParticipation(Participation participation) {
        this.participation = participation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", captureTimestamp=" + captureTimestamp +
                ", participation=" + participation +
                ", path='" + path + '\'' +
                '}';
    }
}
