package at.htl.franklyn.server.feature.telemetry.participation;

import at.htl.franklyn.server.feature.exam.Exam;
import at.htl.franklyn.server.feature.examinee.Examinee;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

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
    public Uni<Participation> getOrCreateParticipation(Examinee examinee, Exam exam) {
        return participationRepository
                .getByExamAndExaminee(examinee.getId(), exam.getId())
                .onItem().ifNull().continueWith(new Participation(examinee, exam))
                .chain(p -> {
                    if (p.getId() == null) {
                        return participationRepository.persist(p);
                    } else {
                        return Uni.createFrom().item(p);
                    }
                });
    }

    public Uni<Boolean> exists(String participationId) {
        return participationRepository
                .count("from Participation where id = ?1", UUID.fromString(participationId))
                .onItem().transform(c -> c != 0);
    }
}
