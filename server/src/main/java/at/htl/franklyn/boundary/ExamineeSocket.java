package at.htl.franklyn.boundary;

import java.net.InetSocketAddress;

import at.htl.franklyn.control.ExamineeRepository;
import io.quarkus.logging.Log;
import io.undertow.websockets.UndertowSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/examinee/{userName}")
@ApplicationScoped
public class ExamineeSocket {
    @Inject
    ExamineeRepository examineeRepository;

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        Log.infof("%s connected", userName);
        examineeRepository.connect(userName, getIpFromSession(session), session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userName") String userName) {
        Log.infof("%s disconnected", userName);
        examineeRepository.disconnect(getIpFromSession(session));
    }

    @OnMessage
    public void onPongMessage(Session session, PongMessage message) {
        Log.infof("received ping");
        examineeRepository.refresh(getIpFromSession((session)), session);
    }

    @OnError
    public void onError(Session session, @PathParam("userName") String userName, Throwable throwable) {
        examineeRepository.disconnect(getIpFromSession(session));
    }

    private String getIpFromSession(Session session) {
        UndertowSession undertowSession = (UndertowSession)session;
        return ((InetSocketAddress)undertowSession.getChannel().remoteAddress()).getHostString();
    }
}
