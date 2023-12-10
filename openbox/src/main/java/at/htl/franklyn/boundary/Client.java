package at.htl.franklyn.boundary;


import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

@ClientEndpoint
public class Client {
    @OnOpen
    public void open(Session session) {
        session.getAsyncRemote().sendText("SERVAS");
    }
}
