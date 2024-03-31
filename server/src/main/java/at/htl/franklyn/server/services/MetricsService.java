package at.htl.franklyn.server.services;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@ApplicationScoped
public class MetricsService {
    private final MeterRegistry registry;
    private final MemoryMXBean memoryMXBean;
    private final File screenshotsFolder;

    MetricsService(MeterRegistry registry, @ConfigProperty(name = "screenshots.path") String screenshotsDirPath) {
        this.registry = registry;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.screenshotsFolder = new File(screenshotsDirPath);

        // Setup Micrometer Metrics
        new ProcessorMetrics().bindTo(registry);
        new DiskSpaceMetrics(new File(screenshotsDirPath)).bindTo(registry);
    }

    public double getSystemCpuUsagePercentage() {
        return registry.get("system.cpu.usage").gauge().value() * 100.0;
    }

    public double getTotalDiskSpaceInBytes() {
        return registry.get("disk.total").gauge().value();
    }

    public double getFreeDiskSpaceInBytes() {
        return registry.get("disk.free").gauge().value();
    }

    public long getScreenshotsFolderSizeInBytes() {
        return getFolderSize(screenshotsFolder);
    }

    public long getTotalMemoryInBytes() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    public long getUsedMemoryInBytes() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    private long getFolderSize(File folder) {
        long bytes = 0;

        for(File file : folder.listFiles()) {
            if(file.isFile()) {
                bytes += file.length();
            } else {
                bytes += getFolderSize(file);
            }
        }

        return bytes;
    }
}
