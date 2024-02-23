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
    public void onOpen(Session session) {
        connectionService.setConnected(true);
    }

    @OnError
    public void onError(Session session) throws IOException {
        connectionService.setConnected(false);
        connectionService.getSession().close();
    }

    @OnMessage
    public void onMessage(String message) {
        // TODO: onScreenshotRequestMessage
    }

    @OnClose
    public void onClose() throws IOException {
        connectionService.setConnected(false);
        connectionService.getSession().close();
    }
}
