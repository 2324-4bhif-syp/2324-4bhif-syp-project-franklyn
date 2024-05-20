package at.htl.franklyn.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "T_PARTICIPATION")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
