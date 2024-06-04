export interface ServerMetrics {
  cpuUsagePercent: number,
  totalDiskSpaceInBytes: number,
  remainingDiskSpaceInBytes: number,
  savedScreenshotsSizeInBytes: number,
  maxAvailableMemoryInBytes: number,
  totalUsedMemoryInBytes: number,
  diagramBackgroundColor: string,
  diagramTextColor: string,
  cpuUtilisationColor: string,
  diskUsageColor: string,
  memoryUtilisationColor: string,
}
