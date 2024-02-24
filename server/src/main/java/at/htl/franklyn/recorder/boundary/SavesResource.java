package at.htl.franklyn.recorder.boundary;

import at.htl.franklyn.server.control.ExamineeRepository;
import at.htl.franklyn.server.entity.Examinee;
import at.htl.franklyn.server.services.ScreenshotService;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;

import java.util.List;

public class SavesResource {

    @Inject
    ScreenshotService screenshotService;

    @Inject
    ExamineeRepository examineeRepository;

    @Scheduled(every = "{update.interval}")
    public void update(){
        // all examinees here should be connected
        List<Examinee> examinees = examineeRepository.findAll();

        for(Examinee examinee : examinees){
            if(examinee.isConnected())
                screenshotService.requestScreenshot(examinee);
        }
    }
}
