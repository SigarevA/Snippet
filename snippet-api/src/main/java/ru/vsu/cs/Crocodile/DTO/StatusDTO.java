package ru.vsu.cs.Crocodile.DTO;

public class StatusDTO {

    public final static String SUCCESS = "success";
    public final static String FAILURE = "failure";

    private String status;

    public StatusDTO() {
    }

    public StatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusDTO setSuccess(){
        this.setStatus("success");
        return this;
    }

    public StatusDTO setFailure(){
        this.setStatus("failure");
        return this;
    }

    @Override
    public String toString() {
        return "StatusDTO{" +
                "status='" + status + '\'' +
                '}';
    }
}