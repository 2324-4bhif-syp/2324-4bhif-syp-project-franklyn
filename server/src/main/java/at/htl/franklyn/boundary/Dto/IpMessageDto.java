package at.htl.franklyn.boundary.Dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record IpMessageDto(List<String> ipAddresses) {
    public static IpMessageDto fromJsonString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        IpMessageDto ipMessage = null;

        try {
            ipMessage = mapper.readValue(json, IpMessageDto.class);
        } catch (JsonProcessingException ignored) {
        }

        return ipMessage;
    }
}
