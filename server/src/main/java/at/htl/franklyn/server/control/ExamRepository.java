package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.Examinee;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExamRepository implements PanacheRepository<Exam> {
    public Set<Integer> getPINsInUse() {
        return getEntityManager()
                .createQuery("select e.pin from Exam e where actualEnd is null", Integer.class)
                .getResultStream()
                .collect(Collectors.toSet());
    }

    public List<Examinee> getExamineesOfExam(long id) {
        return getEntityManager()
                .createQuery(
                """
                select e
                from Participation p join Examinee e on (p.examinee.id = e.id) join Exam ex on (p.exam.id = ex.id)
                where ex.id = ?1
                """, Examinee.class)
                .setParameter(1, id)
                .getResultList();
    }
}
