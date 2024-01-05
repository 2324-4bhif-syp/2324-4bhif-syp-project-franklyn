package at.htl.franklyn.control;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import nu.pattern.OpenCV;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InitBean {

    @ConfigProperty(name = "quarkus.rest-client.server-address.url")
    String url;

    @ConfigProperty(name = "quarkus.http.port")
    int port;

    @ConfigProperty(name = "openbox.port")
    int openBoxPort;

    void startUp(@Observes StartupEvent startupEvent) {
        OpenCV.loadLocally();
        Log.infof("Recorder is listening on: %d", port);
        Log.infof("Franklyn Server Url: %s", url);
        Log.infof("OpenBox is expected to listen on port: %d", openBoxPort);
    }
}
