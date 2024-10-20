package at.htl.franklyn.server.feature.telemetry.participation;

import at.htl.franklyn.server.feature.examinee.Examinee;
import at.htl.franklyn.server.feature.exam.Exam;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "F_PARTICIPATION", uniqueConstraints = @UniqueConstraint(columnNames = { "P_EXAM", "P_EXAMINEE" }))
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "P_ID")
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
        CascadeType.DETACH,
        CascadeType.MERGE,
        CascadeType.PERSIST,
        CascadeType.REFRESH
    })
    @JoinColumn(name = "P_EXAMINEE", nullable = false)
    private Examinee examinee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "P_EXAM", nullable = false)
    private Exam exam;

    public Participation() {
    }

    public Participation(Examinee examinee, Exam exam) {
        this.examinee = examinee;
        this.exam = exam;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Examinee getExaminee() {
        return examinee;
    }

    public void setExaminee(Examinee examinee) {
        this.examinee = examinee;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", examinee=" + examinee +
                ", exam=" + exam +
                '}';
    }
}
