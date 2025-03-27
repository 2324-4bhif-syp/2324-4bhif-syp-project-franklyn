package at.htl.franklyn.server.feature.telemetry.image;

import at.htl.franklyn.server.feature.exam.ExamState;
import at.htl.franklyn.server.feature.telemetry.ScreenshotRequestManager;
import at.htl.franklyn.server.feature.telemetry.command.ExamineeCommandSocket;
import at.htl.franklyn.server.feature.telemetry.participation.Participation;
import at.htl.franklyn.server.feature.telemetry.participation.ParticipationRepository;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    @Inject
    ExamineeCommandSocket commandSocket;

    @Inject
    ScreenshotRequestManager screenshotRequestManager;

    @Inject
    Vertx vertx;

    private Path getScreenshotFolderPath(UUID session) {
        return Paths.get(
                screenshotsPath,
                session.toString()
        );
    }

    public Uni<Void> saveFrameOfSession(UUID session, InputStream frame, FrameType type, boolean isSus) {
        final File imageFile = Paths.get(
                getScreenshotFolderPath(session).toAbsolutePath().toString(),
                String.format("%d.%s", System.currentTimeMillis(), IMG_FORMAT)
        ).toAbsolutePath().toFile();

        return participationRepository
                // fail if participation with given session does not exist
                .findByIdWithExam(session)
                .onItem().ifNull().failWith(new RuntimeException("Session not found"))
                .invoke(Unchecked.consumer(particpation -> {
                            var uploadAllowed = screenshotRequestManager.notifyClientRequestReceived(
                                    particpation.getId()
                            );
                            if (!uploadAllowed) {
                                throw new RuntimeException("Upload not allowed");
                            }
                        })
                )
                // Fail if exam is not ongoing
                .onItem().transform(participation ->
                        participation.getExam().getState() == ExamState.ONGOING
                                ? participation
                                : null
                )
                .onItem().ifNull().failWith(new RuntimeException("Exam not ongoing"))
                .chain(participation -> {
                    Image image = new Image(
                            LocalDateTime.now(),
                            participation,
                            imageFile.getAbsolutePath(),
                            type,
                            isSus
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
                .onItem().ifNull().failWith(new RuntimeException("Unable to read passed frame"))
                .invoke(Unchecked.consumer(newClientFrame -> {
                    if (newClientFrame.getHeight() % 2 != 0 || newClientFrame.getWidth() % 2 != 0) {
                        throw new IllegalStateException("Frame width and height must be divisible by 2");
                    }
                }))
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
                                    // Request new alpha frame so the next client frame can be processed
                                    Log.warnf("No alpha frame found for %s. A new one will be requested", session);
                                    screenshotRequestManager.forceRequestNewAlpha(session)
                                            .subscribe().with(v -> {});
                                    throw new IllegalStateException("Can not store beta frame without previous alpha");
                                }))
                                .onItem()
                                .transform(Unchecked.function(alphaFrameImageEntity -> {
                                    BufferedImage lastAlphaFrame = ImageIO.read(
                                            Paths.get(alphaFrameImageEntity.getPath()).toFile()
                                    );

                                    Graphics2D g = lastAlphaFrame.createGraphics();
                                    g.setComposite(AlphaComposite.SrcOver);
                                    g.drawImage(newClientFrame, 0, 0, null);
                                    g.dispose();

                                    return lastAlphaFrame;
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

    public Uni<Void> deleteAllFramesOfParticipation(Participation p) {
        String folderPath = getScreenshotFolderPath(p.getId()).toString();
        return vertx.fileSystem()
                .exists(folderPath)
                .onItem().transformToUni(exists -> {
                    if (exists) {
                        return vertx.fileSystem().deleteRecursive(folderPath, true);
                    }
                    return Uni.createFrom().voidItem();
                })
                .onItem().transformToUni(v -> imageRepository.deleteImagesOfParticipation(p));
    }

    public Uni<Buffer> loadLatestFrameOfUser(long examId, long userId) {
        return imageRepository
                .getImageByExamAndUser(examId, userId)
                .onItem().ifNull().failWith(new IllegalStateException("No image found to send."))
                .onItem().transformToUni(image -> vertx.fileSystem().readFile(image.getPath()));
    }

    public Uni<Boolean> getSusnessOfImage(long examId, long userId) {
        return imageRepository
                .getImageByExamAndUser(examId, userId)
                .onItem().ifNull().failWith(new IllegalStateException("No image found to send."))
                .onItem().transform(Image::getIsSus);
    }
}
