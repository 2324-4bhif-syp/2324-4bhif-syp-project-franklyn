package at.htl.franklyn.server.boundary.Dto;

public record ServerMetricsDto(
        double cpuUsagePercent,
        double totalDiskSpaceInBytes,
        double remainingDiskSpaceInBytes,
        double savedScreenshotsSizeInBytes,
        double maxAvailableMemoryInBytes,
        double totalUsedMemoryInBytes
) {
}
