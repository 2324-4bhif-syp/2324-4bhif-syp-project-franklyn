package at.htl.franklyn.server.entity.dto;

public record ServerMetricsDto(
        double cpuUsagePercent,
        double totalDiskSpaceInBytes,
        double remainingDiskSpaceInBytes,
        double savedScreenshotsSizeInBytes,
        double maxAvailableMemoryInBytes,
        double totalUsedMemoryInBytes
) {
}
