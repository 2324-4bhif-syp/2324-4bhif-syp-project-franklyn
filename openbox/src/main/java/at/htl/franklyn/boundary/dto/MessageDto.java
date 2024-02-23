package at.htl.franklyn.boundary.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record MessageDto(String request) {
    public static MessageDto fromJson(String json) throws JsonProcessingException {
        return (new ObjectMapper()).readValue(json, MessageDto.class);
    }
}