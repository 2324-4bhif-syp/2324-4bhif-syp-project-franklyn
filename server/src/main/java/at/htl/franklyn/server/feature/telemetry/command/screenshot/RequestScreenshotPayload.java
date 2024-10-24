package at.htl.franklyn.server.feature.telemetry.command.screenshot;

import at.htl.franklyn.server.feature.telemetry.image.FrameType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestScreenshotPayload {
    @JsonProperty("frame_type")
    private final FrameType frameType;

    public RequestScreenshotPayload(FrameType frameType) {
        this.frameType = frameType;
    }

    public FrameType getFrameType() {
        return frameType;
    }
}
