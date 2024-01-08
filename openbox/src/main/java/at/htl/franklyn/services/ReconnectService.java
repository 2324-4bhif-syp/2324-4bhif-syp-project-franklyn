package at.htl.franklyn.services;

import at.htl.franklyn.boundary.Client;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.websocket.ContainerProvider;
import java.net.URI;

public class ReconnectService {
    @ConfigProperty(name = "websocket.url")
    String url;

    @Inject
    UserService userService;

    @Inject
    ConnectionService connectionService;

    @Scheduled(every = "{websocket.reconnection.interval}", skipExecutionIf = ConnectionService.class)
    void tryToEstablishConnection() {
        final URI uri = UriBuilder.fromPath(String.format("%s/examinee/%s", url, userService.getUserName())).build();

        try {
            connectionService.setSession(ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri));
        } catch (Exception ignored){}
    }
}
