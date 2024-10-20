package at.htl.franklyn.server.feature.examinee;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamineeRepostiory implements PanacheRepository<Examinee> {
}
