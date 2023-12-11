package at.htl.franklyn.services;

import at.htl.franklyn.boundary.Client;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

public class ReconnectService {
    @ConfigProperty(name = "websocket.username")
    String username;
    @ConfigProperty(name = "websocket.url")
    String url;

    @Scheduled(every = "10s", skipExecutionIf = ConnectionService.class)
    void tryReconnect() {
        Log.info("Tryin to reconnect");
        final URI uri = URI.create(String.format("%s/examinee/%s", url, username));

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            Log.infof("CONNECTION: %s connected", username);
        } catch (Exception ignored){}
    }
}
