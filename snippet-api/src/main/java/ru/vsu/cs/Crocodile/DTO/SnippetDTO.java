package ru.vsu.cs.Crocodile.DTO;

import ru.vsu.cs.Crocodile.entities.User;

import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;

public class SnippetDTO {

    private String id;
    private String content;
    private Calendar datePublication;
    private AuthorDTO author;
    private boolean favorite;
    private Collection<String> comments;
    private Collection<String> listOfUsersLikedTheSnippet;


    public SnippetDTO(String id, String content, Calendar datePublication, AuthorDTO author, boolean favorite, Collection<String> comments, Collection<String> listOfUsersLikedTheSnippet) {
        this.id = id;
        this.content = content;
        this.datePublication = datePublication;
        this.author = author;
        this.favorite = favorite;
        this.comments = comments;
        this.listOfUsersLikedTheSnippet = listOfUsersLikedTheSnippet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Calendar datePublication) {
        this.datePublication = datePublication;
    }

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    public Collection<String> getComments() {
        return comments;
    }

    public void setComments(Collection<String> comments) {
        this.comments = comments;
    }

    public Collection<String> getListOfUsersLikedTheSnippet() {
        return listOfUsersLikedTheSnippet;
    }

    public void setListOfUsersLikedTheSnippet(Collection<String> listOfUsersLikedTheSnippet) {
        this.listOfUsersLikedTheSnippet = listOfUsersLikedTheSnippet;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "SnippetDTO{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", datePublication=" + datePublication +
                ", author=" + author +
                ", comments=" + comments +
                ", listOfUsersLikedTheSnippet=" + listOfUsersLikedTheSnippet +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SnippetDTO that = (SnippetDTO) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getContent(), that.getContent()) &&
                Objects.equals(getDatePublication(), that.getDatePublication()) &&
                Objects.equals(getAuthor(), that.getAuthor()) &&
                Objects.equals(getComments(), that.getComments()) &&
                Objects.equals(getListOfUsersLikedTheSnippet(), that.getListOfUsersLikedTheSnippet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getContent(), getDatePublication(), getAuthor(), getComments(), getListOfUsersLikedTheSnippet());
    }
}