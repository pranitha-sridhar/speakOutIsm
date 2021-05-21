package com.example.appitup.models;

import java.util.Map;

public class Notification {
    String title;
    String message;
    String complaint_id;
    String comment_id;
    String profile_uri;
    boolean isBlocked = false;
    Map timeStampMap;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public Notification(String title, String message, String complaint_id, String comment_id, String profile_uri, boolean isBlocked) {
        this.title = title;
        this.message = message;
        this.complaint_id = complaint_id;
        this.comment_id = comment_id;
        this.profile_uri = profile_uri;
        this.isBlocked = isBlocked;
    }

    public Notification(String title, String message, String complaint_id, Map timeStampMap) {
        this.title = title;
        this.message = message;
        this.complaint_id = complaint_id;
        this.timeStampMap = timeStampMap;
    }

    public Notification(String title, String message, String complaint_id) {
        this.title = title;
        this.message = message;
        this.complaint_id = complaint_id;
    }

    public String getComplaint_id() {
        return complaint_id;
    }

    public void setComplaint_id(String complaint_id) {
        this.complaint_id = complaint_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getProfile_uri() {
        return profile_uri;
    }

    public void setProfile_uri(String profile_uri) {
        this.profile_uri = profile_uri;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Map getTimeStampMap() {
        return timeStampMap;
    }

    public void setTimeStampMap(Map timeStampMap) {
        this.timeStampMap = timeStampMap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
