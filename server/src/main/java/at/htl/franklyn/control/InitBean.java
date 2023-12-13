package at.htl.franklyn.control;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InitBean {
    @ConfigProperty(name = "quarkus.http.port")
    int port;

    void startUp(@Observes StartupEvent startupEvent) {
        Log.infof("Running on port: %d", port);
    }
}
