package app.model;

public class ClientMessage {

    private ClientPurpose clientPurpose;
    private String message;

    public ClientMessage(ClientPurpose clientPurpose, String message) {
        this.clientPurpose = clientPurpose;
        this.message = message;
    }

    public ClientPurpose getClientPurpose() {
        return clientPurpose;
    }

    public String getMessage() {
        return message;
    }
}
