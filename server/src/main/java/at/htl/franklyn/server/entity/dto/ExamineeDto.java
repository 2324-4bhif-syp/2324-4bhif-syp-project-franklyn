package at.htl.franklyn.server.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExamineeDto(
    String firstname,

    String lastname,

    @JsonProperty("is_connected")
    boolean isConnected
) {
}
