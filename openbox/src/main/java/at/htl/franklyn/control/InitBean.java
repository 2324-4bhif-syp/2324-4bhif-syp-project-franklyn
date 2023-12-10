package at.htl.franklyn.control;

import at.htl.franklyn.boundary.Client;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;

@Startup
@ApplicationScoped
public class InitBean {
    @ConfigProperty(name = "websocket.username", defaultValue = "daswarwohlnichts")
    String username;
    @ConfigProperty(name = "websocket.url")
    String url;

    void startUp(@Observes StartupEvent ev) throws DeploymentException, IOException {
        final URI uri = URI.create(String.format("%s/examinee/%s", url, username));

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            Log.infof("CONNECTION: %s connected", username);
        }
    }
}