package at.htl.franklyn.boundary;

import at.htl.franklyn.boundary.dto.MessageDto;
import at.htl.franklyn.services.ConnectionService;
import at.htl.franklyn.services.ScreenshotService;
import at.htl.franklyn.services.ScreenshotUploadService;
import at.htl.franklyn.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@ClientEndpoint
public class Client {
    @Inject
    UserService userService;

    @Inject
    ConnectionService connectionService;

    @ConfigProperty(name = "http.url")
    String serverUrl;

    @OnOpen
    public void onOpen(Session session) {
        Log.info("Successfully connected to server!");
        connectionService.setConnected(true);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        Log.errorf("Error on Websocket (disconnecting): %s", throwable);
        connectionService.setConnected(false);
        connectionService.getSession().close();
        connectionService.setSession(null);
    }

    @OnMessage
    public void onMessage(String message) {
        MessageDto dto = null;

        try {
            dto = MessageDto.fromJson(message);
        } catch (JsonProcessingException ignored) {
            // Invalid Request
            return;
        }

        if (!dto.request().equals("getScreenshot")) {
            return;
        }

        ScreenshotUploadService screenshotUploadService = RestClientBuilder.newBuilder()
                .baseUri(URI.create(String.format("%s/screenshot/%s", serverUrl, userService.getUserName())))
                .build(ScreenshotUploadService.class);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Response screenshotResponse = ScreenshotService.getScreenshot();

            String frameTyp = screenshotResponse.getHeaderString("Frame-Type");

            ImageIO.write(screenshotResponse.readEntity(BufferedImage.class), "png", byteArrayOutputStream);

            Log.info(frameTyp);

            if(Objects.equals(frameTyp, "alpha")){
                screenshotUploadService.uploadAlphaScreenshot(
                        new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                );
            }
            else {
                screenshotUploadService.uploadBetaScreenshot(
                        new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                );
            }

        } catch(IOException e) {
            Log.error("FAILED: to write image", e);
        } catch (Exception e) {
            Log.error("Image upload failed", e);
        }
    }

    @OnClose
    public void onClose() {
        Log.warn("Lost connection to server");
        connectionService.setConnected(false);
        connectionService.setSession(null);
    }
}
