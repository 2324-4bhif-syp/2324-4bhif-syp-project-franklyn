package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.services.ConnectionStateService;
import at.htl.franklyn.server.services.ParticipationService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/connect/{participationId}")
@ApplicationScoped
public class ExamineeSocket {
    @Inject
    ConnectionStateService stateService;

    @Inject
    ParticipationService participationService;

    // Key: participationId
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    @ConfigProperty(name = "websocket.client-timeout-seconds")
    int clientTimeoutSeconds;

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("participationId") String participationId) {
        if (participationService.exists(participationId)) {
            Log.debugf("%s has connected.", participationId);
            sessions.put(participationId, session);
        } else {
            Log.warnf("An invalid participation id was sent (%s). Is someone tampering with the client?",
                    participationId);
        }
    }

    @OnClose
    public void onClose(
            Session session,
            @PathParam("participationId") String participationId) {
        Log.debugf("%s has lost connection.", participationId);
        sessions.remove(participationId);
        stateService.insertConnected(participationId, false);
    }

    @OnError
    public void onError(
            Session session,
            @PathParam("participationId") String participationId,
            Throwable throwable) {
        Log.debugf("%s has lost connection: ", participationId, throwable);
        sessions.remove(participationId);
        stateService.insertConnected(participationId, false);
    }

    @OnMessage
    public void onPongMessage(
            Session session,
            @PathParam("participationId") String participationId,
            PongMessage pongMessage) {
        stateService.insertConnected(participationId, true);
    }

    @Scheduled(every = "{websocket.ping.interval}")
    public void broadcastPing() {
        final ByteBuffer magic = ByteBuffer.allocate(3);
        magic.put(new byte[]{ 4, 9, 1 });

        sessions.forEach((participationId, session) -> {
            if(session.isOpen()) {
                try {
                    session.getBasicRemote().sendPing(magic);
                } catch (IOException e) {
                    Log.error(e);
                }
            }
        });
    }

    @Scheduled(every = "{websocket.cleanup.interval}")
    public void cleanupDeadExaminees() {
        List<String> timedoutParticipations = stateService.getTimedoutParticipants(clientTimeoutSeconds);

        timedoutParticipations.forEach(p -> {
            Session s = sessions.remove(p);

            if (s != null && s.isOpen()) {
                try {
                    s.close();
                } catch (IOException ignored) {
                }
            }
        });
    }
}
