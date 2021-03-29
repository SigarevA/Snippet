package ru.vsu.cs.Crocodile.entities;

/**
 * @author Sigarev Aleksei
 */
public enum TapeType {
    ALL("all"),
    SUBSCRIPTION("subscription");

    private String text;

    TapeType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static TapeType fromString(String text) {
        for (TapeType b : TapeType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    public static boolean contain(String text) {
        for (TapeType b : TapeType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }
}