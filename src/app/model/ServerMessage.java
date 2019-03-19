package app.model;

public class ServerMessage {

    private ServerPurpose serverPurpose;
    private String message;

    public ServerMessage(ServerPurpose serverPurpose, String message) {
        this.serverPurpose = serverPurpose;
        this.message = message;
    }

    public ServerPurpose getServerPurpose() {
        return serverPurpose;
    }

    public String getMessage() {
        return message;
    }
}
