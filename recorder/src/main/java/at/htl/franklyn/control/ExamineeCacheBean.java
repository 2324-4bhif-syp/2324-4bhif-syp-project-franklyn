package at.htl.franklyn.control;

import at.htl.franklyn.entity.Examinee;
import at.htl.franklyn.services.ExamineesService;
import at.htl.franklyn.services.PingService;
import at.htl.franklyn.services.ScreenshotService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class ExamineeCacheBean {
    @RestClient
    ExamineesService examineesService;

    @ConfigProperty(name = "openbox.port", defaultValue = "8081")
    int openboxPort;

    @Inject
    ExamineeCacheRepository examineeCacheRepository;

    @Scheduled(every = "{cache-update.interval}")
    public void updateCache() {
        List<Examinee> examinees = examineesService.getExaminees();
        boolean hasChanged = examineeCacheRepository.updateCache(examinees);

        if(hasChanged) {
            for(Examinee examinee : examineeCacheRepository.getAllActiveExaminees()) {
                for(String ip : examinee.getIpAddresses()) {
                    PingService pingService = RestClientBuilder.newBuilder()
                            .baseUri(URI.create(String.format("http://%s:%d", ip, openboxPort)))
                            .build(PingService.class);

                    pingService.sendPing()
                            .toCompletableFuture()
                            .orTimeout(1000, TimeUnit.MILLISECONDS)
                            .thenAcceptAsync((response) -> {
                                examineeCacheRepository.saveToCache(examinee.getUserName(), ip);
                            });
                }
            }
        }
    }
}
