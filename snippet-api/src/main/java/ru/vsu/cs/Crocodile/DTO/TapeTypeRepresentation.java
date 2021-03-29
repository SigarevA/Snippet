package ru.vsu.cs.Crocodile.DTO;

public class TapeTypeRepresentation {
    private String name;
    private boolean selected;

    public TapeTypeRepresentation() {
    }

    public TapeTypeRepresentation(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "TapeTypeRepresentation{" +
            "name='" + name + '\'' +
            ", selected=" + selected +
            '}';
    }
}