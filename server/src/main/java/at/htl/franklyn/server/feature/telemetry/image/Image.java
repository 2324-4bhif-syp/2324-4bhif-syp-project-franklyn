package at.htl.franklyn.server.feature.telemetry.image;

import at.htl.franklyn.server.common.Limits;
import at.htl.franklyn.server.feature.telemetry.participation.Participation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
                    + Limits.FILE_PATH_LENGTH_MIN + " and "
                    + Limits.PATH_LENGTH_MAX + " characters",
            min = Limits.FILE_PATH_LENGTH_MIN,
            max = Limits.PATH_LENGTH_MAX
    )
    @Column(name = "I_PATH", nullable = false, length = Limits.PATH_LENGTH_MAX)
    private String path;

    @NotNull(message = "Frame type can not be null")
    @Column(name = "I_FRAME_TYPE", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private FrameType frameType;

    // region additional validation
    @AssertTrue(message = "Frame Type can not be UNSPECIFIED when saving")
    public boolean isFrameTypeSpecified() {
        return frameType != FrameType.UNSPECIFIED;
    }
    // endregion

    public Image() {
    }

    public Image(LocalDateTime captureTimestamp, Participation participation, String path, FrameType frameType) {
        this.captureTimestamp = captureTimestamp;
        this.participation = participation;
        this.path = path;
        this.frameType = frameType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Image capture timestamp can not be null") @PastOrPresent(message = "Can not persist image captured in the future") LocalDateTime getCaptureTimestamp() {
        return captureTimestamp;
    }

    public void setCaptureTimestamp(@NotNull(message = "Image capture timestamp can not be null") @PastOrPresent(message = "Can not persist image captured in the future") LocalDateTime captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public @NotNull(message = "Participation can not be null") Participation getParticipation() {
        return participation;
    }

    public void setParticipation(@NotNull(message = "Participation can not be null") Participation participation) {
        this.participation = participation;
    }

    public @NotBlank(message = "Path to image can not be blank") @Size(
            message = "Image path must have a length between "
                    + Limits.FILE_PATH_LENGTH_MIN + " and "
                    + Limits.PATH_LENGTH_MAX + " characters",
            min = Limits.FILE_PATH_LENGTH_MIN,
            max = Limits.PATH_LENGTH_MAX
    ) String getPath() {
        return path;
    }

    public void setPath(@NotBlank(message = "Path to image can not be blank") @Size(
            message = "Image path must have a length between "
                    + Limits.FILE_PATH_LENGTH_MIN + " and "
                    + Limits.PATH_LENGTH_MAX + " characters",
            min = Limits.FILE_PATH_LENGTH_MIN,
            max = Limits.PATH_LENGTH_MAX
    ) String path) {
        this.path = path;
    }

    public @NotNull(message = "Frame type can not be null") FrameType getFrameType() {
        return frameType;
    }

    public void setFrameType(@NotNull(message = "Frame type can not be null") FrameType frameType) {
        this.frameType = frameType;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", captureTimestamp=" + captureTimestamp +
                ", participation=" + participation +
                ", path='" + path + '\'' +
                ", frameType=" + frameType +
                '}';
    }
}
