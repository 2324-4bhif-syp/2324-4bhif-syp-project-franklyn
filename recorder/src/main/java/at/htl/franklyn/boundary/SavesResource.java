package at.htl.franklyn.boundary;

import at.htl.franklyn.control.ExamineeCacheRepository;
import at.htl.franklyn.entity.Examinee;
import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.ScreenshotService;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SavesResource {
    @RestClient
    ExamineesService examineesService;
    @RestClient
    ScreenshotService screenshotService;

    @Inject
    ExamineeCacheRepository examineeCacheRepository;

    @ConfigProperty(name = "timestamp.pattern")
    String timestampPattern;

    @ConfigProperty(name = "openbox.port")
    int openboxPort;

    @Scheduled(every = "{update.interval}")
    public void update(){
        // all examinees here should be connected
        Map<String, String> ipAddresses = examineeCacheRepository.getAllCachedExaminees();

        for(String examinee : ipAddresses.keySet()){
            try {
                File directory = new File(String.format("screenshots/%s-%s", ipAddresses.get(examinee), examinee));

                if(!directory.exists()){
                    directory.mkdirs();
                }

                screenshotService = RestClientBuilder.newBuilder()
                        .baseUri(URI.create(String.format("http://%s:%d", ipAddresses.get(examinee), openboxPort)))
                        .build(ScreenshotService.class);

                ByteArrayInputStream bis = new ByteArrayInputStream(screenshotService.getScreenshot());

                ImageIO.write(
                        ImageIO.read(bis),
                        "png",
                        new File(String.format("%s/%s-%s.png",
                            directory.getPath(), 
                            ipAddresses.get(examinee),
                            new SimpleDateFormat(timestampPattern).format(new Date())))
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
