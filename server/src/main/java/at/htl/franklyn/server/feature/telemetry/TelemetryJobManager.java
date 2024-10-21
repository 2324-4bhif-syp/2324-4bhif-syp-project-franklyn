package at.htl.franklyn.server.feature.telemetry;

import at.htl.franklyn.server.feature.exam.Exam;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.quartz.*;

@ApplicationScoped
public class TelemetryJobManager {
    private static final String FRANKLYN_TELEMETRY_JOB_GROUP = "franklynTelemetryJobGroup";
    private static final String FRANKLYN_TELEMETRY_JOB_PREFIX = "franklynTelemetryJob-";
    private static final String FRANKLYN_TELEMETRY_TRIGGER_GROUP = "franklynTelemetryTriggerGroup";
    private static final String FRANKLYN_TELEMETRY_TRIGGER_PREFIX = "franklynTelemetryTrigger-";
    private static final String FRANKLYN_TELEMETRY_JOB_EXAM_ID_JOB_DATA_KEY = "examId";

    @Inject
    Scheduler quartz;

    private String getTelemetryJobIdentity(long examId) {
        return String.format("%s%d", FRANKLYN_TELEMETRY_JOB_PREFIX, examId);
    }

    private String getTelemetryJobTriggerIdentity(long examId) {
        return String.format("%s%d", FRANKLYN_TELEMETRY_TRIGGER_PREFIX, examId);
    }

    public Uni<Void> startTelemetryJob(Exam exam) {
        JobDetail job = JobBuilder.newJob(TelemetryJob.class)
                .withIdentity(getTelemetryJobIdentity(exam.getId()), FRANKLYN_TELEMETRY_JOB_GROUP)
                .usingJobData(FRANKLYN_TELEMETRY_JOB_EXAM_ID_JOB_DATA_KEY, exam.getId())
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTelemetryJobTriggerIdentity(exam.getId()), FRANKLYN_TELEMETRY_TRIGGER_GROUP)
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(exam.getScreencaptureInterval().intValue())
                                .repeatForever())
                .build();
        try {
            quartz.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            Log.error("Could not create telemetry job", e);
            return Uni.createFrom().failure(e);
        }

        return Uni.createFrom().voidItem();
    }

    public Uni<Void> stopTelemetryJob(Exam exam) {
        boolean found;
        try {
            found = quartz.deleteJob(new JobKey(getTelemetryJobIdentity(exam.getId()), FRANKLYN_TELEMETRY_JOB_GROUP));
        } catch (SchedulerException e) {
            Log.error("Could not stop telemetry job", e);
            return Uni.createFrom().failure(e);
        }

        return Uni.createFrom().voidItem();
    }

    public static class TelemetryJob implements Job {

        @Inject
        ExamineeCommandSocket commandSocket;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

            // TODO: request screenshots
            Log.infof("Requesting screenshots of examinees in exam id = %d",
                    dataMap.getLong(FRANKLYN_TELEMETRY_JOB_EXAM_ID_JOB_DATA_KEY));
        }
    }
}
