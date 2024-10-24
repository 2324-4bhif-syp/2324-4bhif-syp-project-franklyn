package at.htl.franklyn.server.feature.telemetry.command;

public class CommandBase {
    private final CommandType type;
    private final Object payload;

    public CommandBase(CommandType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public CommandType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}