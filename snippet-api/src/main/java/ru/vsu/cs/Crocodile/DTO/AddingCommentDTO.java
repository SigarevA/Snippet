package ru.vsu.cs.Crocodile.DTO;

public class AddingCommentDTO {

    private String snippetId;
    private String content;

    public AddingCommentDTO() {
    }

    public AddingCommentDTO(String snippetId, String content) {
        this.snippetId = snippetId;
        this.content = content;
    }

    public String getSnippetId() {
        return snippetId;
    }

    public void setSnippetId(String snippetId) {
        this.snippetId = snippetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AddingCommentDTO{" +
                "snippetId='" + snippetId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}