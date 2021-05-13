package com.example.appitup.models;

import java.io.Serializable;

public class Comment implements Serializable {
    String username;

    String commentId;

    String comment;

    public Comment(String username, String commentId, String comment) {
        this.username = username;
        this.commentId = commentId;
        this.comment = comment;
    }

    public Comment(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
