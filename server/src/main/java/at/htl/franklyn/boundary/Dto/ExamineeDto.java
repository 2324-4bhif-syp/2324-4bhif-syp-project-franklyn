package at.htl.franklyn.boundary.Dto;

import java.util.List;

public record ExamineeDto(String username, List<String> ipAddresses, boolean connected) {
}
