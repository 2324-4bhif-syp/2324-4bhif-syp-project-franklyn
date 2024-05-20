package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.entity.dto.ExamineeDto;
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

    public List<ExamineeDto> getExamineesOfExamWithConnectionState(long id) {
        return getEntityManager()
                .createQuery(
                """
                select new at.htl.franklyn.server.entity.dto.ExamineeDto(e.firstname, e.lastname, cs.isConnected)
                from Participation p join Examinee e on (p.examinee.id = e.id)
                    join Exam ex on (p.exam.id = ex.id)
                    join ConnectionState cs on (p.id = cs.participation.id)
                where ex.id = ?1
                """, ExamineeDto.class)
                .setParameter(1, id)
                .getResultList();
    }
}
