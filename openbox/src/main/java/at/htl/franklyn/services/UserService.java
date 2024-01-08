package at.htl.franklyn.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class UserService {
    @ConfigProperty(name = "websocket.username")
    String usernameProperty;

    private String userName;

    public String getUserName() {
        String returnString = userName;

        if (userName == null) {
            returnString = usernameProperty;
        }

        return returnString;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
