package at.htl.franklyn.server.feature.telemetry.connection;

import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ConnectionStateService {
    @Inject
    ConnectionStateRepository stateRepository;

    @Inject
    ParticipationRepository participationRepository;

    public Uni<Void> insertConnected(String participationId, boolean state) {
        return participationRepository
                .findById(UUID.fromString(participationId))
                .onItem().ifNotNull()
                .transform(p -> new ConnectionState(
                        LocalDateTime.now(ZoneOffset.UTC),
                        p,
                        state
                ))
                .chain(cs -> stateRepository.persist(cs))
                .replaceWithVoid();
    }

    public Uni<List<String>> getTimedoutParticipants(int timeout) {
        return stateRepository.getTimedoutParticipants(timeout);
    }
}
