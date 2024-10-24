package at.htl.franklyn.server.feature.exam;

import at.htl.franklyn.server.feature.examinee.ExamineeDto;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ExamRepository implements PanacheRepository<Exam> {
    @Inject
    Mutiny.SessionFactory sf;

    public Uni<Set<Integer>> getPINsInUse() {
        return sf.withSession(session -> session
                .createQuery("select e.pin from Exam e where actualEnd is null", Integer.class)
                .getResultList()
                .onItem().transform(HashSet::new)
        );
    }

    public Uni<List<ExamineeDto>> getExamineesOfExamWithConnectionState(long id) {
        return sf.withSession(session ->
                session.createQuery(
                    """
                    select new at.htl.franklyn.server.feature.examinee.ExamineeDto(
                        e.firstname,
                        e.lastname,
                        cs.isConnected
                    )
                    from Participation p join Examinee e on (p.examinee.id = e.id)
                        join ConnectionState cs on (p.id = cs.participation.id)
                    where p.exam.id = ?1
                        and cs.pingTimestamp = (
                            select max(c.pingTimestamp)
                                from ConnectionState c
                                where c.participation.id = p.id
                        )
                    """, ExamineeDto.class)
                .setParameter(1, id)
                .getResultList()
        );
    }
}