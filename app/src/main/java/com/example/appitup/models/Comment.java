package com.example.appitup.models;

import java.io.Serializable;
import java.util.Map;

public class Comment implements Serializable {
    String username;

    String commentId;

    String comment;

    Map timeStampMap;

    String timeStampStr;

    public Comment(String username, String commentId, String comment) {
        this.username = username;
        this.commentId = commentId;
        this.comment = comment;
    }

    public Comment(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }

    public Comment(String username, String comment, Map timeStampMap) {
        this.username = username;
        this.comment = comment;
        this.timeStampMap = timeStampMap;
    }

    public Comment(String username, String commentId, String comment, Map timeStampMap, String timeStampStr) {
        this.username = username;
        this.commentId = commentId;
        this.comment = comment;
        this.timeStampMap = timeStampMap;
        this.timeStampStr = timeStampStr;
    }

    public Comment(String username, String commentId, String comment, Map timeStampMap) {
        this.username = username;
        this.commentId = commentId;
        this.comment = comment;
        this.timeStampMap = timeStampMap;
    }

    public Map getTimeStampMap() {
        return timeStampMap;
    }

    public void setTimeStampMap(Map timeStampMap) {
        this.timeStampMap = timeStampMap;
    }

    public String getTimeStampStr() {
        return timeStampStr;
    }

    public void setTimeStampStr(String timeStampStr) {
        this.timeStampStr = timeStampStr;
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
