package at.htl.franklyn.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

@Entity
@Table(name = "F_IMAGE")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "I_ID")
    Long id;

    @NotNull(message = "Image capture timestamp can not be null")
    @PastOrPresent(message = "Can not persist image captured in the future")
    @Column(name = "I_CAPTURE_TIMESTAMP", nullable = false)
    LocalDateTime captureTimestamp;

    @NotNull(message = "Participation can not be null")
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "I_PARTICIPATION_ID", nullable = false)
    Participation participation;

    @NotBlank(message = "Path to image can not be blank")
    @Column(name = "I_PATH", nullable = true, length = 4095) // 4095 = Linux path limit
    String path;
}
