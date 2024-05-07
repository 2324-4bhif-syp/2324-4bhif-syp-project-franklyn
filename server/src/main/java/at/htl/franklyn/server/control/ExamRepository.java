package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Exam;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

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
}
