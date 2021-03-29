package ru.vsu.cs.Crocodile.DTO;

public class RegistrationDTO extends AuthDTO {
    private String name;

    public RegistrationDTO() {
    }

    public RegistrationDTO(String name, String email, String password) {
        super(email, password);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RegistrationDTO{" +
            "name='" + name + '\'' +
            "email=" + getEmail() + "\'" +
            "password=\'" + getPassword() + "\'" +
            '}';
    }
}