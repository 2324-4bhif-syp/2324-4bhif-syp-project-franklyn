package at.htl.franklyn.services;

import at.htl.franklyn.boundary.Client;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.websocket.ContainerProvider;
import java.net.URI;

public class ReconnectService {
    @ConfigProperty(name = "websocket.username")
    String username;
    @ConfigProperty(name = "websocket.url")
    String url;

    @Inject
    ConnectionService connectionService;

    @Scheduled(every = "{websocket.reconnection.interval}", skipExecutionIf = ConnectionService.class)
    void tryToEstablishConnection() {
        final URI uri = URI.create(String.format("%s/examinee/%s", url, username));

        try {
            connectionService.setSession(ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri));
        } catch (Exception ignored){}
    }
}
