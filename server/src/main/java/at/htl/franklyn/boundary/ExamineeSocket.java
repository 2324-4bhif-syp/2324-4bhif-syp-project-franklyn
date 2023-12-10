package at.htl.franklyn.boundary;

import java.net.InetSocketAddress;

import at.htl.franklyn.control.ExamineeRepository;
import io.undertow.websockets.UndertowSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;

@ServerEndpoint("/examinee/{userName}")
@ApplicationScoped
public class ExamineeSocket {
    @Inject
    ExamineeRepository examineeRepository;

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        examineeRepository.connect(userName, getIpFromSession(session));
    }

    @OnClose
    public void onClose(Session session, @PathParam("userName") String userName) {
        examineeRepository.disconnect(getIpFromSession(session));
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
