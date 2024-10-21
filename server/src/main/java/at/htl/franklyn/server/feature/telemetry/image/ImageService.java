package at.htl.franklyn.server.feature.telemetry.image;

import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ApplicationScoped
public class ImageService {
    private static final String IMG_FORMAT = "png";
    @ConfigProperty(name = "screenshots.path")
    String screenshotsPath;

    @Inject
    ParticipationRepository participationRepository;

    @Inject
    ImageRepository imageRepository;

    public Uni<Void> saveFrameOfSession(UUID session, InputStream frame, FrameType type) {
        final File imageFile = Paths.get(
                screenshotsPath,
                session.toString(),
                String.format("%d.%s", System.currentTimeMillis(), IMG_FORMAT)
        ).toAbsolutePath().toFile();

        return participationRepository
                .findById(session)
                .onItem()
                .ifNull().fail()
                .chain(participation -> {
                    Image image = new Image(
                            LocalDateTime.now(ZoneOffset.UTC),
                            participation,
                            imageFile.getAbsolutePath(),
                            type
                    );
                    return imageRepository.persist(image).replaceWithVoid();
                })
                .invoke(Unchecked.consumer(v -> {
                    File frameDirectory = Paths.get(screenshotsPath, session.toString()).toFile();

                    if (!frameDirectory.exists() && !frameDirectory.mkdirs()) {
                        throw new RuntimeException("Could not create screenshot directory for session!");
                    }
                }))
                .onItem()
                .transform(Unchecked.function(v -> ImageIO.read(frame)))
                .onItem()
                .ifNull().fail()
                .chain(newClientFrame -> {
                    // Beta frame needs processing before it can be saved
                    // Merge with last alpha frame then save
                    if (type == FrameType.BETA) {
                        return imageRepository.find(
                                        """
                                        participation.id = ?1 \
                                        and captureTimestamp = (\
                                            select max(captureTimestamp) from Image i \
                                                where i.participation.id = ?1 and frameType = ?2\
                                        ) and frameType = ?2
                                        """,
                                        session,
                                        FrameType.ALPHA
                                )
                                .firstResult()
                                .onItem().ifNull().failWith(Unchecked.supplier(() -> {
                                    // TODO: request new alpha frame
                                    throw new IllegalStateException("Can not store beta frame without previous alpha");
                                }))
                                .onItem()
                                .transform(Unchecked.function(alphaFrameImageEntity -> {
                                    BufferedImage lastAlphaFrame = ImageIO.read(
                                            Paths.get(alphaFrameImageEntity.getPath()).toFile()
                                    );

                                    int width = lastAlphaFrame.getWidth();
                                    int height = lastAlphaFrame.getHeight();
                                    BufferedImage betaFrame = new BufferedImage(
                                            width,
                                            height,
                                            BufferedImage.TYPE_INT_ARGB
                                    );

                                    for (int y = 0; y < height; y++) {
                                        for (int x = 0; x < width; x++) {
                                            int alphaRGB = lastAlphaFrame.getRGB(x, y);
                                            int diffRGB = newClientFrame.getRGB(x, y);

                                            if (0 != diffRGB) {
                                                betaFrame.setRGB(x, y, diffRGB);
                                            } else {
                                                betaFrame.setRGB(x, y, alphaRGB);
                                            }
                                        }
                                    }
                                    return betaFrame;
                                }));
                    } else {
                        return Uni.createFrom().item(newClientFrame);
                    }
                })
                .invoke(Unchecked.consumer(img -> {
                    ImageIO.write(
                            img,
                            IMG_FORMAT,
                            imageFile
                    );
                }))
                .replaceWithVoid();
    }
}
