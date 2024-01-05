package at.htl.franklyn.boundary;

import java.net.InetSocketAddress;

import at.htl.franklyn.boundary.Dto.IpMessageDto;
import at.htl.franklyn.control.ExamineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.undertow.websockets.UndertowSession;
import io.vertx.core.net.impl.URIDecoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.Path;

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
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        IpMessageDto ipMessage = IpMessageDto.fromJsonString(message);

        if (ipMessage != null) {
            examineeRepository.updateIpAddresses(decodeUsername(username), ipMessage.ipAddresses());
        }
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
