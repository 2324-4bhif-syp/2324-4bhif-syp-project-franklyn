package at.htl.franklyn.server.services;

import at.htl.franklyn.server.entity.Examinee;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.Comparator;
import java.util.stream.Stream;

@ApplicationScoped
public class ScreenshotService {
    @ConfigProperty(name = "screenshots.path")
    String screenshotsDirPath;

    public void requestScreenshot(Examinee examinee){
        examinee
                .getSession()
                .getAsyncRemote()
                .sendText("{\"request\": \"getScreenshot\"}");
    }

    public boolean deleteAllScreenshots() {
        Path screenshotsPath = Path.of(screenshotsDirPath);
        boolean success = true;

        try(Stream<Path> files = Files.walk(screenshotsPath)) {
            files
                    .filter(p -> !p.equals(screenshotsPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            success = false;
        }

        return success;
    }
}
