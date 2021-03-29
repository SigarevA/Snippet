package ru.vsu.cs.Crocodile.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Sigarev Aleksei
 */
@Document(collection = "Comment")
public class Comment {

    @Id
    private String id;
    private String content;
    private String author;
    private Calendar datePublication;
    private Collection<String> likedUser;

    public Comment() {
    }


    public Comment(String id, String content, String author, Calendar datePublication, Collection<String> likedUser) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.datePublication = datePublication;
        this.likedUser = likedUser;
    }

    public Collection<String> getLikedUser() {
        return likedUser;
    }

    public void setLikedUser(Collection<String> likedUser) {
        this.likedUser = likedUser;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Calendar getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Calendar datePublication) {
        this.datePublication = datePublication;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", datePublication=" + datePublication +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}