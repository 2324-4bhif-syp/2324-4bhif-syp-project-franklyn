package at.htl.franklyn.server.feature.telemetry;

import at.htl.franklyn.server.feature.exam.Exam;
import at.htl.franklyn.server.feature.telemetry.command.ExamineeCommandSocket;
import at.htl.franklyn.server.feature.telemetry.image.FrameType;
import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import io.quarkus.logging.Log;
import io.quarkus.vertx.core.runtime.context.VertxContextSafetyToggle;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.smallrye.common.vertx.VertxContext;
import org.hibernate.reactive.mutiny.Mutiny;
import org.quartz.*;
import io.vertx.core.Context;

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

        return found
                ? Uni.createFrom().voidItem()
                : Uni.createFrom().failure(new RuntimeException("Could not complete Exam. No job active"));
    }

    public static class TelemetryJob implements Job {
        @Inject
        ExamineeCommandSocket commandSocket;

        @Inject
        ParticipationRepository participationRepository;

        @Inject
        Mutiny.SessionFactory sf;

        @Inject
        Vertx vertx;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            long examId = dataMap.getLong(FRANKLYN_TELEMETRY_JOB_EXAM_ID_JOB_DATA_KEY);

            Context context = VertxContext.getOrCreateDuplicatedContext(vertx);
            VertxContextSafetyToggle.setContextSafe(context, true);
            context.runOnContext(event -> {
                sf.withSession(session -> {
                    return participationRepository
                            .getParticipationsOfExam(examId)
                            .toMulti()
                            .flatMap(list -> Multi.createFrom().iterable(list))
                            .onItem().transformToUniAndConcatenate(participation ->
                                    commandSocket.requestFrame(participation.getId(), FrameType.UNSPECIFIED)
                                            .onFailure().recoverWithNull())
                            // Chain must stay on same thread to avoid killing hibernate or else we get a
                            // "Detected use of the reactive Session from a different Thread"
                            .emitOn(r -> context.runOnContext(ignored -> r.run()))
                            .toUni();
                })
                .subscribe().with(ignored -> {});
            });
        }
    }
}
