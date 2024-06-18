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

    /**
     * Depending on whether a participation with the given examinee and exam exists or not this function either:
     * 1) Creates a new participation and returns it
     * 2) returns the existing participation
     * @param examinee examinee part of the participation
     * @param exam exam part of the participation
     * @return a participation
     */
    public Participation getOrCreateParticipation(Examinee examinee, Exam exam) {
        Participation p = participationRepository.getByExamAndExaminee(examinee.getId(), exam.getId());

        if (p == null) {
            p = new Participation(examinee, exam);
            participationRepository.persist(p);
        }

        return p;
    }

    public boolean exists(String participationId) {
        return participationRepository.count("from Participation where id = ?1", participationId) != 0;
    }
}
