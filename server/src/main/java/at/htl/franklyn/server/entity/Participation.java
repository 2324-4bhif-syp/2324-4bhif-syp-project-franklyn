package at.htl.franklyn.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "T_PARTICIPATION")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
        CascadeType.DETACH,
        CascadeType.MERGE,
        CascadeType.PERSIST,
        CascadeType.REFRESH
    })
    @JoinColumn(name = "P_EXAMINEE", nullable = false)
    Examinee examinee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "P_EXAM", nullable = false)
    Exam exam;
}
