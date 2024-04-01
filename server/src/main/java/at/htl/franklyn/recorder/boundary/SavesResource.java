package at.htl.franklyn.recorder.boundary;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quartz.*;

@ApplicationScoped
public class SavesResource {
    @ConfigProperty(name = "update.interval")
    int updateInterval;
    @Inject
    org.quartz.Scheduler quartz;

    private Trigger trigger;

    public void scheduleScreenshotUpdateJob() {
        JobDetail job = JobBuilder.newJob(ScreenshotUpdateJob.class)
                .withIdentity(ScreenshotUpdateJob.class.getName(), "updateGroup")
                .build();
        this.trigger = createTriggerWith(this.updateInterval);

        try {
            this.quartz.scheduleJob(job, trigger);
        } catch (Exception ignored) {
            Log.error("ERROR: failed to schedule screenshot update job");
        }
    }

    public void rescheduleScreenshotUpdateJob(int updateInterval) {
        Trigger nextTrigger = createTriggerWith(updateInterval);

        try {
            this.quartz.rescheduleJob(this.trigger.getKey(), nextTrigger);
        } catch (SchedulerException ignored) {
            Log.error("ERROR: failed to reschedule screenshot update job");
        }

        this.trigger = nextTrigger;
    }

    private Trigger createTriggerWith(int updateInterval) {
        return TriggerBuilder.newTrigger()
                .startNow()
                .withIdentity("updateTrigger", "updateGroup")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(updateInterval)
                                .repeatForever()
                )
                .build();
    }
}

