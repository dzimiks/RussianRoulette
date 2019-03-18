package app.model;

import java.io.Serializable;

public class Message implements Serializable {

    private String id;
    private Purpose purpose;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", purpose=" + purpose +
                '}';
    }
}
