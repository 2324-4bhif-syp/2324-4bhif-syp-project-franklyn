package at.htl.franklyn.server.feature.telemetry.image;

import at.htl.franklyn.server.feature.telemetry.participation.Participation;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImageRepository implements PanacheRepository<Image> {
    public Uni<Void> deleteImagesOfParticipation(Participation p) {
        return delete("participation.id = ?1", p.getId())
                .replaceWithVoid();
    }
}
