package at.htl.franklyn.server.feature.examinee;

import at.htl.franklyn.server.common.Limits;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExamineeDto(
        @NotNull(message = "Firstname can not be null")
        @NotBlank(message = "Firstname can not be blank")
        @Size(
                message = "Firstname must have a length between "
                        + Limits.EXAMINEE_FIRSTNAME_LENGTH_MIN + " and "
                        + Limits.EXAMINEE_FIRSTNAME_LENGTH_MAX + " characters",
                min = Limits.EXAMINEE_FIRSTNAME_LENGTH_MIN,
                max = Limits.EXAMINEE_FIRSTNAME_LENGTH_MAX
        )
        String firstname,

        @NotNull(message = "Lastname can not be null")
        @NotBlank(message = "Lastname can not be blank")
        @Size(
                message = "Lastname must have a length between "
                        + Limits.EXAMINEE_LASTNAME_LENGTH_MIN + " and "
                        + Limits.EXAMINEE_LASTNAME_LENGTH_MAX + " characters",
                min = Limits.EXAMINEE_LASTNAME_LENGTH_MIN,
                max = Limits.EXAMINEE_LASTNAME_LENGTH_MAX
        )
        String lastname,

        @JsonProperty("is_connected")
        boolean isConnected
) {
}
