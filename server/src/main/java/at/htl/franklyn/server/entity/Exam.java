package at.htl.franklyn.server.entity;

import at.htl.franklyn.server.control.Limits;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "F_EXAM")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "E_ID")
    private Long id;

    @NotNull(message = "Start timestamp can not be null!")
    @FutureOrPresent(message = "Start for a new exam can not be in the past")
    @Column(name = "E_PLANNED_START", nullable = false)
    private LocalDateTime plannedStart;

    @NotNull(message = "End timestamp can not be null!")
    @FutureOrPresent(message = "End for a new exam can not be in the past")
    @Column(name = "E_PLANNED_END", nullable = false)
    private LocalDateTime plannedEnd;

    @PastOrPresent(message = "Actual end time can not be in the future")
    @Column(name = "E_ACTUAL_END", nullable = true)
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

    public Exam() {
    }

    public Exam(LocalDateTime plannedStart, LocalDateTime plannedEnd, LocalDateTime actualEnd, String title, int pin, ExamState state) {
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.actualEnd = actualEnd;
        this.title = title;
        this.pin = pin;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDateTime plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public ExamState getState() {
        return state;
    }

    public void setState(ExamState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", plannedStart=" + plannedStart +
                ", plannedEnd=" + plannedEnd +
                ", actualEnd=" + actualEnd +
                ", title='" + title + '\'' +
                ", pin=" + pin +
                ", state=" + state +
                '}';
    }
}
