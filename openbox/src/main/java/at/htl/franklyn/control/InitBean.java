package at.htl.franklyn.control;

import at.htl.franklyn.services.UserService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InitBean {
    @ConfigProperty(name = "websocket.url")
    String url;

    @Inject
    UserService userService;

    void startUp(@Observes StartupEvent startupEvent) {
        Log.infof("Connecting to server: %s", url);
    }
}
