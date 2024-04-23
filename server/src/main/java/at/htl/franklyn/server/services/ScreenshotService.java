package at.htl.franklyn.server.services;

import at.htl.franklyn.server.control.ExamineeRepository;
import at.htl.franklyn.server.entity.Examinee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public class ScreenshotService {
    @ConfigProperty(name = "screenshots.path")
    String screenshotsDirPath;

    @Inject
    ExamineeRepository examineeRepository;

    public void requestScreenshot(Examinee examinee){
        examinee
                .getSession()
                .getAsyncRemote()
                .sendText("{\"request\": \"getScreenshot\"}");
    }

    public void requestAlphaFrame(String username){

        Optional<Examinee> examinee = examineeRepository
                .findAll()
                .stream()
                .filter(e -> e.getUsername().equals(username))
                .findFirst();

        if(examinee.isEmpty()){
            return;
        }

        examinee
                .get()
                .getSession()
                .getAsyncRemote()
                .sendText("{\"request\": \"getAlphaFrame\"}");
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
