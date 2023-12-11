package at.htl.franklyn.boundary;


import at.htl.franklyn.services.ConnectionService;
import jakarta.inject.Inject;
import jakarta.websocket.*;

@ClientEndpoint
public class Client {
    @Inject
    ConnectionService connectionService;

    @OnOpen
    public void open(Session session) {
        connectionService.setConnected(true);
    }

    @OnClose
    public void close(Session session) {
        System.out.println("SERVER CLOSED I CLOSED I DONT KNOW");
    }

    @OnError
    public void error(Throwable throwable) {
        //connectionService.setConnected(false);
        System.out.println("WHUT ERROR");
    }
}
