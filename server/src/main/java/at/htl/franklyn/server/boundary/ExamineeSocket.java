package at.htl.franklyn.server.boundary;

import at.htl.franklyn.server.services.ConnectionStateService;
import at.htl.franklyn.server.services.ParticipationService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.websockets.next.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@WebSocket(path = "/connect/{participationId}")
public class ExamineeSocket {
    @Inject
    ConnectionStateService stateService;

    @Inject
    ParticipationService participationService;

    @Inject
    WebSocketConnection connection;

    @Inject
    OpenConnections openConnections;

    // Key: participationId
    //private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    @ConfigProperty(name = "websocket.client-timeout-seconds")
    int clientTimeoutSeconds;

    @OnOpen
    @WithSession
    public Uni<Void> onOpen() {
        String participationId = connection.pathParam("participationId");
        Log.infof("Hello %s", participationId);
        return participationService.exists(participationId)
                .onItem().invoke(exists -> {
                    if (exists) {
                        Log.infof("%s has connected.", participationId);
                    } else {
                        Log.warnf("An invalid participation id was sent (%s). Is someone tampering with the client?",
                                participationId);
                    }
                })
                .replaceWithVoid();
    }

    @OnClose
    @WithTransaction
    public Uni<Void> onClose() {
        String participationId = connection.pathParam("participationId");
        Log.infof("%s has lost connection.", participationId);
        return stateService.insertConnected(participationId, false);
    }

    @OnError
    @WithTransaction
    public Uni<Void> onError(Exception e) {
        String participationId = connection.pathParam("participationId");
        Log.infof("%s has lost connection: %s", participationId, e);
        return stateService.insertConnected(participationId, false);
    }

    @OnPongMessage
    @WithTransaction
    public Uni<Void> onPongMessage(Buffer data) {
        String participationId = connection.pathParam("participationId");

        return stateService.insertConnected(participationId, true);
    }

    @Scheduled(every = "{websocket.ping.interval}")
    public Uni<Void> broadcastPing() {
        final Buffer magic = Buffer.buffer(new byte[]{4, 9, 1});
        return Multi.createFrom().iterable(
                        openConnections.stream().map(c -> c.sendPing(magic)).toList()
                )
                .onItem().transformToUniAndConcatenate(u -> u)
                .toUni();
    }

    @Scheduled(every = "{websocket.cleanup.interval}")
    @WithTransaction
    public Uni<Void> cleanupDeadExaminees() {
        return stateService.getTimedoutParticipants(clientTimeoutSeconds)
                .onItem().transformToMulti(p -> Multi.createFrom().iterable(p))
                .onItem().transform(pId -> {
                    WebSocketConnection s = openConnections
                            .stream()
                            .filter(c -> c.pathParam("participationId").equals(pId))
                            .findFirst()
                            .orElse(null);

                    Log.infof("Disconnecting %s (Reason: Timed out)", pId);

                    return Uni.join().all(
                            stateService.insertConnected(pId, false),
                            s != null && s.isOpen() ? s.close() : Uni.createFrom().voidItem()
                    ).andFailFast().replaceWithVoid();
                })
                .onItem().transformToUniAndConcatenate(a -> a)
                .toUni()
                .replaceWithVoid();
    }
}
