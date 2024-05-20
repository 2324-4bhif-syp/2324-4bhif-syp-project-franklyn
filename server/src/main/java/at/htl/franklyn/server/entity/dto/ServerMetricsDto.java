package at.htl.franklyn.server.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServerMetricsDto(
        @JsonProperty("cpu_usage_percent")
        double cpuUsagePercent,

        @JsonProperty("total_disk_space_in_bytes")
        double totalDiskSpaceInBytes,

        @JsonProperty("remaining_disk_space_in_bytes")
        double remainingDiskSpaceInBytes,

        @JsonProperty("saved_screenshots_size_in_bytes")
        double savedScreenshotsSizeInBytes,

        @JsonProperty("max_available_memory_in_bytes")
        double maxAvailableMemoryInBytes,

        @JsonProperty("total_used_memory_in_bytes")
        double totalUsedMemoryInBytes
) {
}
