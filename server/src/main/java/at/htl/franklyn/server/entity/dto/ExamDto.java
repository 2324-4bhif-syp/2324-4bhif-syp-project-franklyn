package at.htl.franklyn.server.entity.dto;

import at.htl.franklyn.server.control.Limits;
import at.htl.franklyn.server.entity.Exam;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ExamDto(
    @NotNull(message = "Title of exam can not be null")
    @NotBlank(message = "Title of exam can not be blank")
    @Size(
            message = "Length of the exam title must be between "
                    + Limits.EXAM_PIN_MIN_VALUE + " and "
                    + Limits.EXAM_TITLE_LENGTH_MAX + " characters",
            min = Limits.EXAM_TITLE_LENGTH_MIN,
            max = Limits.EXAM_TITLE_LENGTH_MAX
    )
    String title,

    @NotNull(message = "Start timestamp can not be null!")
    @FutureOrPresent(message = "Start for a new exam can not be in the past")
    LocalDateTime start,

    @NotNull(message = "End timestamp can not be null!")
    @FutureOrPresent(message = "End for a new exam can not be in the past")
    LocalDateTime end
) {
}
