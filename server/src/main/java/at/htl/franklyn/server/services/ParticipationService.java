package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ParticipationRepository;
import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.entity.Participation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ParticipationService {
    @Inject
    ParticipationRepository participationRepository;

    public Participation getOrCreateParticipation(Examinee examinee, Exam exam) {
        Participation p = participationRepository.getByExamAndExaminee(examinee.getId(), exam.getId());

        if (p == null) {
            p = new Participation(examinee, exam);
            participationRepository.persist(p);
        }

        return p;
    }
}
