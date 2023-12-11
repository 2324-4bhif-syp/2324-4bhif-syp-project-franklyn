package at.htl.franklyn.boundary;

import at.htl.franklyn.services.ConnectionService;
import jakarta.inject.Inject;
import jakarta.websocket.*;

import java.io.IOException;

@ClientEndpoint
public class Client {
    @Inject
    ConnectionService connectionService;

    @OnOpen
    public void onOpen() {
        connectionService.setConnected(true);
    }

    @OnClose
    public void onClose() throws IOException {
        connectionService.setConnected(false);
        connectionService.getSession().close();
    }
}
