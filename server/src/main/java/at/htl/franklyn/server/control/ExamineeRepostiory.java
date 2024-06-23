package at.htl.franklyn.server.control;

import at.htl.franklyn.server.entity.Examinee;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamineeRepostiory implements PanacheRepository<Examinee> {
}
