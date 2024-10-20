package at.htl.franklyn.server.feature.exam;

import at.htl.franklyn.server.common.Limits;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

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
        LocalDateTime end,

        @NotNull(message = "Screencapture interval can not be null")
        @Min(
                message = "Screencapture interval can not be less than " + Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS,
                value = Limits.EXAM_MIN_CAPTURE_INTERVAL_SECONDS
        )
        @Max(
                message = "Screencapture interval can not be more than " + Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS,
                value = Limits.EXAM_MAX_CAPTURE_INTERVAL_SECONDS
        )
        @JsonProperty("screencapture_interval_seconds")
        Long screencaptureIntervalSeconds
) {
    @AssertTrue(message = "Start timestamp can not be before end timestamp")
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
