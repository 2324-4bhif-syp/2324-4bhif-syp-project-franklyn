package at.htl.franklyn.control;

import at.htl.franklyn.services.UserService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class AppLifecycleBean {
    @Inject
    UserService userService;

    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() == LaunchMode.NORMAL) {
            boolean isValidName = false;

            do {
                Log.info("Enter your name: ");
                String name = System.console().readLine();

                if (!name.isBlank()) {
                    userService.setUserName(name);
                    Log.info("Username: " + userService.getUserName() + "\n");
                    isValidName = true;
                } else {
                    Log.warn("Please enter a valid name.");
                }
            } while (!isValidName);
        }
    }

}