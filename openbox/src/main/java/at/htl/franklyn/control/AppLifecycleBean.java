package at.htl.franklyn.control;

import at.htl.franklyn.services.ConnectionService;
import at.htl.franklyn.services.UserService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@ApplicationScoped
public class AppLifecycleBean {
    @ConfigProperty(name = "websocket.url")
    String url;

    @ConfigProperty(name = "websocket.username", defaultValue = "")
    String devUserName;

    @Inject
    ConnectionService connectionService;

    @Inject
    UserService userService;

    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() == LaunchMode.NORMAL) {
            boolean isValidName = false;

            do {
                System.out.print("Enter your name: ");
                String name = System.console().readLine();

                if (!name.isBlank()) {
                    userService.setUserName(name);
                    isValidName = true;
                    userService.setInitialized(true);
                } else {
                    Log.warn("Please enter a valid name.");
                }
            } while (!isValidName);
        } else if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            // Only print server when in dev mode
            Log.infof("Connecting to server: %s", url);

            // When in dev-mode use username from application.properties
            userService.setUserName(devUserName);
            userService.setInitialized(true);
        }

        Log.info("Username: " + userService.getUserName() + "\n");
    }

    void onShutdown(@Observes ShutdownEvent ev) throws IOException {
        Log.info("Shutting down!");

        // Only disconnect if the client is still connected
        if (connectionService.getSession() != null) {
            connectionService.getSession().close();
        }
    }
}