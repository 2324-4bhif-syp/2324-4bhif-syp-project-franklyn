package at.htl.franklyn.server.feature.telemetry.command.screenshot;

import at.htl.franklyn.server.feature.telemetry.command.CommandBase;
import at.htl.franklyn.server.feature.telemetry.command.CommandType;

public class RequestScreenshotCommand extends CommandBase {
    public RequestScreenshotCommand(RequestScreenshotPayload payload) {
        super(CommandType.CAPTURE_SCREEN, payload);
    }
}
