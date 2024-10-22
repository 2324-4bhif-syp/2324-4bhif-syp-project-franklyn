package at.htl.franklyn.server.feature.exam;

import at.htl.franklyn.server.common.Limits;
import at.htl.franklyn.server.feature.examinee.ExamineeDto;
import at.htl.franklyn.server.feature.telemetry.TelemetryJobManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class ExamService {
    @Inject
    ExamRepository examRepository;

    @Inject
    TelemetryJobManager telemetryJobManager;

    /**
     * Creates a new Exam from a Dto.
     * This business logic method assigns additional parameters which are not included in the dto.
     * e.g. The pin and state parameters.
     * Finally, The Exam is persisted and returned to the caller.
     * @param examDto exam dto from which a new exam is built
     * @return the newly created exam
     */
    public Uni<Exam> createExam(ExamDto examDto) {
        return getFreePIN()
                .onItem().transform(p -> {
                    Exam exam = new Exam();
                    exam.setTitle(examDto.title());
                    exam.setPlannedStart(examDto.start());
                    exam.setPlannedEnd(examDto.end());
                    exam.setScreencaptureInterval(examDto.screencaptureIntervalSeconds());

                    // Initial exam state is always created
                    exam.setState(ExamState.CREATED);
                    exam.setPin(p); // TODO: Fix potential Race condition
                    return exam;
                })
                .chain(examRepository::persist);

    }

    /**
     * Returns wether or not an exam with the given id exists
     * @param id exam id to check
     * @return true - when an exam with the given id exists otherwise false
     */
    public Uni<Boolean> exists(long id) {
        return examRepository.count("from Exam where id = ?1", id)
                .onItem().transform(c -> c != 0);
    }

    /**
     * Queries a list of examinees for an exam with the given id
     * @param id exam id for which to query students for
     * @return list of examinee DTOs holding most of the relevant data of an exminee needed during the exam
     */
    public Uni<List<ExamineeDto>> getExamineesOfExam(long id) {
        return examRepository.getExamineesOfExamWithConnectionState(id);
    }

    /**
     * Logically starts an exam. This includes starting telemetry collection and setting the state to ongoing
     * @param e exam to be started
     * @return boolean indication whether the exam could be started or not
     */
    public Uni<Void> startExam(Exam e) {
        if (e.getState() != ExamState.CREATED) {
            return Uni.createFrom().failure(new IllegalStateException("Invalid exam state for startExam"));
        }

        return examRepository
                .update("state = ?1, actualStart = ?2 where id = ?3",
                        ExamState.ONGOING,
                        LocalDateTime.now(ZoneOffset.UTC),
                        e.getId())
                .chain(affectedRows -> telemetryJobManager.startTelemetryJob(e));
    }

    /**
     * Logically completes an exam. This includes stopping telemetry collection, disconnecting openbox clients
     * and setting the state to done
     * @param e exam to be completed
     * @return boolean indicating whether the exam could be completed successfully
     */
    public Uni<Void> completeExam(Exam e) {
        if (e.getState() != ExamState.ONGOING) {
            return Uni.createFrom().failure(new IllegalStateException("Invalid exam state for completeExam"));
        }

        return examRepository
                .update("state = ?1, actualEnd = ?2 where id = ?3",
                        ExamState.DONE,
                        LocalDateTime.now(ZoneOffset.UTC),
                        e.getId())
                .chain(affectedRows -> telemetryJobManager.stopTelemetryJob(e));
        // TODO: disconnect openbox clients
    }

    /**
     * Checks whether the given pin belongs to an active exam, and thus is valid, or not
     * @param pin pin to check
     * @return true - pin is valid (belongs to an active exam) otherwise false
     */
    public Uni<Boolean> isValidPIN(int pin) {
        return examRepository.count("from Exam e where e.actualEnd is null and pin = ?1", pin)
                .onItem().transform(c -> c != 0);
    }

    /**
     * Returns the active exam with the given pin
     * @param pin pin to search for
     * @return the exam with the given pin
     */
    public Uni<Exam> findByPIN(int pin) {
        return examRepository
                .find("from Exam e where e.actualEnd is null and pin = ?1", pin)
                .firstResult();
    }

    /**
     * Generates a new random PIN to be used for a new exam.
     * This function can theoretically loop endlessly if 1000 Exams are active at once.
     * @return a free PIN
     */
    private Uni<Integer> getFreePIN() {
        Random rnd = new Random();
        return examRepository.getPINsInUse()
                .onItem().transform(takenPINs -> {
                    int pin;
                    do {
                        pin = rnd.nextInt(Limits.EXAM_PIN_MIN_VALUE, Limits.EXAM_PIN_MAX_VALUE + 1);
                    } while(takenPINs.contains(pin));

                    return pin;
                });
    }
}
