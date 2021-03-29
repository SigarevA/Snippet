package ru.vsu.cs.Crocodile.DTO;

public class AuthDTO {

    private String email;
    private String password;

    public AuthDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthDTO{" +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}