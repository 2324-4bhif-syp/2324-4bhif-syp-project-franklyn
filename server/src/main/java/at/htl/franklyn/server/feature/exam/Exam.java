package at.htl.franklyn.server.feature.exam;

import at.htl.franklyn.server.common.Limits;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "F_EXAM", uniqueConstraints = @UniqueConstraint(columnNames = { "E_ACTUAL_END", "E_PIN" }))
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "E_ID")
    private Long id;

    @NotNull(message = "Start timestamp can not be null!")
    @FutureOrPresent(message = "Start for a new exam can not be in the past")
    @Column(name = "E_PLANNED_START", nullable = false)
    @JsonProperty("planned_start")
    private LocalDateTime plannedStart;

    @NotNull(message = "End timestamp can not be null!")
    @FutureOrPresent(message = "End for a new exam can not be in the past")
    @Column(name = "E_PLANNED_END", nullable = false)
    @JsonProperty("planned_end")
    private LocalDateTime plannedEnd;

    @PastOrPresent(message = "Actual start time can not be in the future")
    @Column(name = "E_ACTUAL_START", nullable = true)
    @JsonProperty("actual_start")
    private LocalDateTime actualStart;

    @PastOrPresent(message = "Actual end time can not be in the future")
    @Column(name = "E_ACTUAL_END", nullable = true)
    @JsonProperty("actual_end")
    private LocalDateTime actualEnd;

    @NotNull(message = "Title of exam can not be null")
    @NotBlank(message = "Title of exam can not be blank")
    @Size(
            message = "Length of exam title must be between "
                    + Limits.EXAM_PIN_MIN_VALUE + " and "
                    + Limits.EXAM_TITLE_LENGTH_MAX + " characters",
            min = Limits.EXAM_TITLE_LENGTH_MIN,
            max = Limits.EXAM_TITLE_LENGTH_MAX
    )
    @Column(name = "E_TITLE", nullable = false, length = Limits.EXAM_TITLE_LENGTH_MAX)
    private String title;

    @Min(message = "PIN can not be smaller than " + Limits.EXAM_PIN_MIN_VALUE, value = Limits.EXAM_PIN_MIN_VALUE)
    @Max(message = "PIN can not be larger than " + Limits.EXAM_PIN_MAX_VALUE, value = Limits.EXAM_PIN_MAX_VALUE)
    @Column(name = "E_PIN", nullable = false)
    private int pin;

    @NotNull(message = "Exam state can not be null")
    @Column(name = "E_STATE", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ExamState state;

    @NotNull(message = "Screencapture interval can not be null")
    @Column(name = "E_SCREENCAPTURE_INTERVAL", nullable = false)
    @Min(
            message = "Screencapture interval can not be less than " + Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS
    )
    @Max(
            message = "Screencapture interval can not be more than " + Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS
    )
    @JsonProperty("screencapture_interval_seconds")
    private Long screencaptureInterval;

    public Exam() {
    }

    public Exam(LocalDateTime plannedStart, LocalDateTime plannedEnd, LocalDateTime actualStart, LocalDateTime actualEnd, String title, int pin, ExamState state, Long screencaptureInterval) {
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
        this.title = title;
        this.pin = pin;
        this.state = state;
        this.screencaptureInterval = screencaptureInterval;
    }

    // region generated getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull(message = "Start timestamp can not be null!") @FutureOrPresent(message = "Start for a new exam can not be in the past") LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(@NotNull(message = "Start timestamp can not be null!") @FutureOrPresent(message = "Start for a new exam can not be in the past") LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public @NotNull(message = "End timestamp can not be null!") @FutureOrPresent(message = "End for a new exam can not be in the past") LocalDateTime getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(@NotNull(message = "End timestamp can not be null!") @FutureOrPresent(message = "End for a new exam can not be in the past") LocalDateTime plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public @PastOrPresent(message = "Actual start time can not be in the future") LocalDateTime getActualStart() {
        return actualStart;
    }

    public void setActualStart(@PastOrPresent(message = "Actual start time can not be in the future") LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public @PastOrPresent(message = "Actual end time can not be in the future") LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(@PastOrPresent(message = "Actual end time can not be in the future") LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    public @NotNull(message = "Title of exam can not be null") @NotBlank(message = "Title of exam can not be blank") @Size(
            message = "Length of exam title must be between "
                    + Limits.EXAM_PIN_MIN_VALUE + " and "
                    + Limits.EXAM_TITLE_LENGTH_MAX + " characters",
            min = Limits.EXAM_TITLE_LENGTH_MIN,
            max = Limits.EXAM_TITLE_LENGTH_MAX
    ) String getTitle() {
        return title;
    }

    public void setTitle(@NotNull(message = "Title of exam can not be null") @NotBlank(message = "Title of exam can not be blank") @Size(
            message = "Length of exam title must be between "
                    + Limits.EXAM_PIN_MIN_VALUE + " and "
                    + Limits.EXAM_TITLE_LENGTH_MAX + " characters",
            min = Limits.EXAM_TITLE_LENGTH_MIN,
            max = Limits.EXAM_TITLE_LENGTH_MAX
    ) String title) {
        this.title = title;
    }

    @Min(message = "PIN can not be smaller than " + Limits.EXAM_PIN_MIN_VALUE, value = Limits.EXAM_PIN_MIN_VALUE)
    @Max(message = "PIN can not be larger than " + Limits.EXAM_PIN_MAX_VALUE, value = Limits.EXAM_PIN_MAX_VALUE)
    public int getPin() {
        return pin;
    }

    public void setPin(@Min(message = "PIN can not be smaller than " + Limits.EXAM_PIN_MIN_VALUE, value = Limits.EXAM_PIN_MIN_VALUE) @Max(message = "PIN can not be larger than " + Limits.EXAM_PIN_MAX_VALUE, value = Limits.EXAM_PIN_MAX_VALUE) int pin) {
        this.pin = pin;
    }

    public @NotNull(message = "Exam state can not be null") ExamState getState() {
        return state;
    }

    public void setState(@NotNull(message = "Exam state can not be null") ExamState state) {
        this.state = state;
    }

    public @NotNull(message = "Screencapture interval can not be null") @Min(
            message = "Screencapture interval can not be less than " + Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS
    ) @Max(
            message = "Screencapture interval can not be more than " + Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS
    ) Long getScreencaptureInterval() {
        return screencaptureInterval;
    }

    public void setScreencaptureInterval(@NotNull(message = "Screencapture interval can not be null") @Min(
            message = "Screencapture interval can not be less than " + Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS
    ) @Max(
            message = "Screencapture interval can not be more than " + Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS,
            value = Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS
    ) Long screencaptureInterval) {
        this.screencaptureInterval = screencaptureInterval;
    }

    // endregion
}
