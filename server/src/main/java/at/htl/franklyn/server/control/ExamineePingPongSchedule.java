package at.htl.franklyn.server.control;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

@ApplicationScoped
public class ExamineePingPongSchedule {
    @Inject
    ExamineeRepository examineeRepository;

    @ConfigProperty(name = "websocket.client-timeout-seconds")
    int clientTimeoutSeconds;

    @Scheduled(every = "{websocket.ping.interval}")
    public void sendPing() {
        examineeRepository.findAll().forEach(e -> {
            if (e.isConnected() && e.getSession().isOpen()) {
                // Allocate small data buffer for ping which is sent to the client
                ByteBuffer x = ByteBuffer.allocate(3);
                x.put(new byte[]{ 4, 2, 0 });
                try {
                    e.getSession().getBasicRemote().sendPing(x);
                } catch (IOException ex) {
                    Log.error(ex);
                }
            }
        });
    }

    @Scheduled(every = "{websocket.cleanup.interval}")
    public void checkPing() {
        examineeRepository.findAll().forEach(e -> {
            if(e.isConnected()
                    && e.getLastPingTimestamp().isBefore(LocalDateTime.now().minusSeconds(clientTimeoutSeconds))
                    && e.getSession().isOpen()
            ) {
                Log.infof("Disconnecting: %s (reason: timed out)", e.getUsername());
                examineeRepository.disconnect(e.getUsername());
            }
        });
    }
}
