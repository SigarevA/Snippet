package ru.vsu.cs.Crocodile.DTO;

import java.util.Objects;

public class CommentIDDTO {

    private String commentId;

    public CommentIDDTO() {
    }

    public CommentIDDTO(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    @Override
    public String toString() {
        return "CommentIDDTO{" +
                "commentId='" + commentId + '\'' +
                '}';
    }
}