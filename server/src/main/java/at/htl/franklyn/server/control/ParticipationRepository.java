package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Participation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParticipationRepository implements PanacheRepository<Participation> {
}
