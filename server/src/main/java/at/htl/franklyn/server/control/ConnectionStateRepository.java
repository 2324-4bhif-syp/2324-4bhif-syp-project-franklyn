package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.ConnectionState;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ConnectionStateRepository implements PanacheRepository<ConnectionState> {
    public List<String> getTimedoutParticipants(int timeout) {
        return getEntityManager().createNamedQuery("ConnectionState.getTimedoutParticipants", String.class)
                .setParameter("timeout", timeout)
                .getResultList();
    }
}
