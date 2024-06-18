package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ConnectionStateRepository;
import at.htl.franklyn.server.control.ParticipationRepository;
import at.htl.franklyn.server.entity.ConnectionState;
import at.htl.franklyn.server.entity.Participation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class ConnectionStateService {
    @Inject
    ConnectionStateRepository stateRepository;

    @Inject
    ParticipationRepository participationRepository;

    public void insertConnected(String participationId, boolean state) {
        Participation p = participationRepository
                .findById(participationId);

        // given participation is invalid
        if (p == null) {
            return;
        }

        ConnectionState connectionState = new ConnectionState(
                LocalDateTime.now(ZoneOffset.UTC),
                p,
                state
        );

        stateRepository.persist(connectionState);
    }

    public List<String> getTimedoutParticipants(int timeout) {
        return stateRepository.getTimedoutParticipants(timeout);
    }
}
