package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.ConnectionState;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ConnectionStateRepository implements PanacheRepository<ConnectionState> {

    public Uni<List<String>> getTimedoutParticipants(int timeout) {
        return getSession()
                .chain(session -> session
                        .createNamedQuery("ConnectionState.getTimedoutParticipants", String.class)
                        .setParameter(1, timeout)
                        .getResultList());
    }
}
