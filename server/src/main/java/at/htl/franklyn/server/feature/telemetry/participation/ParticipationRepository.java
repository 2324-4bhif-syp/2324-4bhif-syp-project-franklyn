package at.htl.franklyn.server.feature.telemetry.participation;

import at.htl.franklyn.server.feature.exam.Exam;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ParticipationRepository implements PanacheRepositoryBase<Participation, UUID> {
    public Uni<Participation> getByExamAndExaminee(long examineeId, long examId) {
        return find(
                """
                        select p From Participation p where p.exam.id = ?2 and p.examinee.id = ?1
                        """, examineeId, examId)
                .firstResult();
    }

    public Uni<Participation> findByIdWithExam(UUID sessionID) {
        return find("from Participation p left join fetch p.exam where p.id = ?1", sessionID).firstResult();
    }

    public Uni<List<Participation>> getParticipationsOfExam(long examId) {
        return find("exam.id = ?1", examId).list();
    }

    public Uni<Long> getParticipationCountOfExam(long examId) {
        return count("exam.id = ?1", examId);
    }

    public Uni<List<Participation>> getParticipationsOfExam(Exam e) {
        return getParticipationsOfExam(e.getId());
    }

    public Uni<Participation> findByIdWitExaminee(UUID sessionID) {
        return find("from Participation p left join fetch p.examinee where p.id = ?1", sessionID).firstResult();
    }
}
