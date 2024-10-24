package at.htl.franklyn.server.feature.telemetry.participation;

import at.htl.franklyn.server.feature.exam.Exam;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ParticipationRepository implements PanacheRepositoryBase<Participation, UUID> {
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Participation> getByExamAndExaminee(long examineeId, long examId) {
        return sf.withSession(session -> session
                .createQuery(
                    """
                    select p From Participation p where p.exam.id = ?2 and p.examinee.id = ?1
                    """, Participation.class)
                .setParameter(1, examineeId)
                .setParameter(2, examId)
                .getResultList()
                .onItem().transform(i -> i.stream().findFirst().orElse(null))
        );
    }

    public Uni<List<Participation>> getParticipationsOfExam(long examId) {
        return find("exam.id = ?1", examId).list();
    }

    public Uni<List<Participation>> getParticipationsOfExam(Exam e) {
        return getParticipationsOfExam(e.getId());
    }
}
