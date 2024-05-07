package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ExamRepository;
import at.htl.franklyn.server.control.Limits;
import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.ExamState;
import at.htl.franklyn.server.entity.dto.ExamDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Random;
import java.util.Set;

@ApplicationScoped
public class ExamService {
    @Inject
    ExamRepository examRepository;

    /**
     * Creates a new Exam from a Dto.
     * This business logic method assigns additional parameters which are not included in the dto.
     * e.g. The pin and state parameters.
     * Finally, The Exam is persisted and returned to the caller.
     * @param examDto exam dto from which a new exam is built
     * @return the newly created exam
     */
    public Exam createExam(ExamDto examDto) {
        Exam exam = new Exam();
        exam.setTitle(examDto.title());
        exam.setPlannedStart(examDto.start());
        exam.setPlannedEnd(examDto.end());

        // Initial exam state is always created
        exam.setState(ExamState.CREATED);
        exam.setPin(getFreePIN()); // TODO: Fix potential Race condition

        examRepository.persist(exam);

        return exam;
    }

    /**
     * Generates a new random PIN to be used for a new exam.
     * This function can theoretically loop endlessly if 1000 Exams are active at once.
     * @return a free PIN
     */
    private int getFreePIN() {
        Random rnd = new Random();
        Set<Integer> takenPINs = examRepository.getPINsInUse();

        int pin;
        do {
            pin = rnd.nextInt(Limits.EXAM_PIN_MIN_VALUE, Limits.EXAM_PIN_MAX_VALUE + 1);
        } while(takenPINs.contains(pin));

        return pin;
    }
}
