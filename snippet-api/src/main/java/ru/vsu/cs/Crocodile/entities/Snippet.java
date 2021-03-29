package ru.vsu.cs.Crocodile.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Sigarev Aleksei
 */
@Document(collection = "snippets")
public class Snippet {

    @Id
    private String id;
    private String content;
    private Calendar datePublication;
    private String author;
    private Collection<String> comments;
    private Collection<String> listOfUsersLikedTheSnippet;

    public Snippet(String id, String content, Calendar datePublication) {
        this.id = id;
        this.content = content;
        this.datePublication = datePublication;
    }

    public Snippet() {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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

    @Override
    public String toString() {
        return "Snippet{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", datePublication=" + datePublication +
                ", author=" + author +
                ", comments=" + comments +
                ", ListOfUsersLikedTheSnippet=" + listOfUsersLikedTheSnippet +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snippet snippet = (Snippet) o;
        return Objects.equals(getId(), snippet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
