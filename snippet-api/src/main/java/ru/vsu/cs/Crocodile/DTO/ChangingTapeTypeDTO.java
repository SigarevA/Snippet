package ru.vsu.cs.Crocodile.DTO;

public class ChangingTapeTypeDTO {
    public String name;

    public ChangingTapeTypeDTO() {
    }

    public ChangingTapeTypeDTO(String name) {
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
        return "ChangingTapeTypeDTO{" +
            "name='" + name + '\'' +
            '}';
    }
}