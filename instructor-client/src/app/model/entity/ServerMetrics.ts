export interface ServerMetrics {
  cpuUsagePercent: number,
  totalDiskSpaceInBytes: number,
  remainingDiskSpaceInBytes: number,
  savedScreenshotsSizeInBytes: number,
  maxAvailableMemoryInBytes: number,
  totalUsedMemoryInBytes: number,
  timerId: number | undefined
}
