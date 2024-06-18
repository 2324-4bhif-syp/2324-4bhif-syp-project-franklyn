package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.entity.Participation;
import at.htl.franklyn.server.entity.dto.ExamineeDto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParticipationRepository implements PanacheRepositoryBase<Participation, String> {
    public Participation getByExamAndExaminee(long examineeId, long examId) {
        return getEntityManager()
                .createQuery(
                        """
                        select p From Participation p where p.exam.id = ?2 and p.examinee.id = ?1
                        """, Participation.class)
                .setParameter(1, examineeId)
                .setParameter(2, examId)
                .getResultList()
                .stream().findFirst().orElse(null);
    }
}
