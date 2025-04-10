package at.htl.franklyn.server.feature.examinee;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExamineeRepostiory implements PanacheRepository<Examinee> {
    public Uni<List<Examinee>> getExamineesOfExam(long examId) {
        return list("""
                select e
                    from Participation p
                        join Examinee e on (p.examinee.id = e.id and p.exam.id = ?1)
                """, examId);
    }

    public Uni<Examinee> getExamineeWithSessionId(UUID sessionId) {
        return find("""
                select e
                    from Participation p
                        join Examinee e on (p.examinee.id = e.id and p.id = ?1)
                """, sessionId).firstResult();
    }
}
