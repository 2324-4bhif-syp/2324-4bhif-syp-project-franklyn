package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ExamRepository;
import at.htl.franklyn.server.control.Limits;
import at.htl.franklyn.server.entity.Exam;
import at.htl.franklyn.server.entity.ExamState;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.entity.dto.ExamDto;
import at.htl.franklyn.server.entity.dto.ExamineeDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
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
     * Returns wether or not an exam with the given id exists
     * @param id exam id to check
     * @return true - when an exam with the given id exists otherwise false
     */
    public boolean exists(long id) {
        return examRepository.count("from Exam where id = ?1", id) != 0;
    }

    /**
     * Queries a list of examinees for an exam with the given id
     * @param id exam id for which to query students for
     * @return list of examinee DTOs holding most of the relevant data of an exminee needed during the exam
     */
    public List<ExamineeDto> getExamineesOfExam(long id) {
        return examRepository.getExamineesOfExamWithConnectionState(id);
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
