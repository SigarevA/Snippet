package ru.vsu.cs.Crocodile.DTO;

public class RepresentationAuthorDTO extends AuthorDTO{

    private boolean subscription;

    public RepresentationAuthorDTO(String id, String name, boolean subscription) {
        super(id, name);
        this.subscription = subscription;
    }

    public boolean isSubscription() {
        return subscription;
    }

    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "RepresentationAuthorDTO{" +
                "subscription=" + subscription +
                ", id='" + this.getId() + '\'' +
                ", name='" + this.getName() + '\'' +
                '}';
    }
}