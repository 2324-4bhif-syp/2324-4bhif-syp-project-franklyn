package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.control.ExamineeRepository;
import io.quarkus.logging.Log;
import io.vertx.core.net.impl.URIDecoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/examinee/{username}")
@ApplicationScoped
public class ExamineeSocket {
    @Inject
    ExamineeRepository examineeRepository;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        String decodedUsername = decodeUsername(username);
        Log.infof("%s connected", decodedUsername);
        examineeRepository.connect(decodedUsername, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        String decodedUsername = decodeUsername(username);
        Log.infof("%s disconnected", decodedUsername);
        examineeRepository.disconnect(decodedUsername);
    }

    @OnMessage
    public void onPongMessage(Session session, @PathParam("username") String username, PongMessage message) {
        examineeRepository.refresh(decodeUsername(username), session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        examineeRepository.disconnect(decodeUsername(username));
    }

    public String decodeUsername(String encodedUsername) {
        return URIDecoder.decodeURIComponent(encodedUsername);
    }
}
