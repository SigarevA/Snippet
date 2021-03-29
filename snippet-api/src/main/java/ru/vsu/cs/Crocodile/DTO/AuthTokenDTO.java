package ru.vsu.cs.Crocodile.DTO;

public class AuthTokenDTO extends StatusDTO {
    private String token;
    private String userId;

    public AuthTokenDTO(String status, String token, String userId) {
        super(status);
        this.token = token;
        this.userId = userId;
    }

    public AuthTokenDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AuthTokenDTO{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}