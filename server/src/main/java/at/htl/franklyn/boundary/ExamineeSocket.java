package at.htl.franklyn.boundary;

import java.net.InetSocketAddress;

import at.htl.franklyn.boundary.Dto.IpMessageDto;
import at.htl.franklyn.control.ExamineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.undertow.websockets.UndertowSession;
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
        Log.infof("%s connected", username);
        examineeRepository.connect(username, username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        Log.infof("%s disconnected", username);
        examineeRepository.disconnect(username);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        IpMessageDto ipMessage = IpMessageDto.fromJsonString(message);

        Log.infof("%s", ipMessage); // TOOD: remove

        if (ipMessage != null) {
            examineeRepository.updateIpAddresses(username, ipMessage.ipAddresses());
        }
    }

    @OnMessage
    public void onPongMessage(Session session, @PathParam("username") String username, PongMessage message) {
        examineeRepository.refresh(username, session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        examineeRepository.disconnect(username);
    }
}
