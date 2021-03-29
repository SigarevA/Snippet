package ru.vsu.cs.Crocodile.DTO;

public class MessageDTO extends StatusDTO {

    private String message;

    public MessageDTO(String status, String message) {
        super(status);
        this.message = message;
    }

    public MessageDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "message='" + message + '\'' +
                '}';
    }
}