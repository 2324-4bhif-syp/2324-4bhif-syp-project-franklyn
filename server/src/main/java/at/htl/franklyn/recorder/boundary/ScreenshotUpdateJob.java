package at.htl.franklyn.recorder.boundary;

import at.htl.franklyn.server.control.ExamineeRepository;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.services.ScreenshotService;
import jakarta.inject.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

public class ScreenshotUpdateJob implements Job {
    @Inject
    ScreenshotService screenshotService;
    @Inject
    ExamineeRepository examineeRepository;

    public void execute(JobExecutionContext ctx) {
        // all examinees here should be connected
        List<Examinee> examinees = examineeRepository.findAll();

        for(Examinee examinee : examinees){
            if(examinee.isConnected())
                screenshotService.requestScreenshot(examinee);
        }
    }
}
