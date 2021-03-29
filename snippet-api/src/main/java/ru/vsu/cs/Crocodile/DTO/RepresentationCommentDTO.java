package ru.vsu.cs.Crocodile.DTO;

import java.util.Calendar;
import java.util.Collection;

public class RepresentationCommentDTO {

    private String id;
    private AuthorDTO authorDTO;
    private boolean favorite;
    private Calendar datePublication;
    private String content;
    private Collection<String> likedUser;

    public RepresentationCommentDTO(String id, AuthorDTO authorDTO, boolean favorite, Calendar datePublication, String content, Collection<String> likedUser) {
        this.id = id;
        this.authorDTO = authorDTO;
        this.favorite = favorite;
        this.datePublication = datePublication;
        this.content = content;
        this.likedUser = likedUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthorDTO getAuthorDTO() {
        return authorDTO;
    }

    public void setAuthorDTO(AuthorDTO authorDTO) {
        this.authorDTO = authorDTO;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Calendar getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Calendar datePublication) {
        this.datePublication = datePublication;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Collection<String> getLikedUser() {
        return likedUser;
    }

    public void setLikedUser(Collection<String> likedUser) {
        this.likedUser = likedUser;
    }

    @Override
    public String toString() {
        return "RepresentationCommentDTO{" +
                "id='" + id + '\'' +
                ", authorDTO=" + authorDTO +
                ", favorite=" + favorite +
                ", datePublication=" + datePublication +
                ", content='" + content + '\'' +
                ", likedUser=" + likedUser +
                '}';
    }
}