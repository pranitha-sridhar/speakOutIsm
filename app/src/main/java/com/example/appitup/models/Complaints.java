package com.example.appitup.models;

public class Complaints {
    String username;
    String uid;
    String subject;
    String body;
    String category;
    String subcategory;
    String visibility;
    String status;

    public Complaints(String username, String uid, String subject, String body, String category, String subcategory, String visibilty, String status) {
        this.username = username;
        this.uid = uid;
        this.subject = subject;
        this.body = body;
        this.category = category;
        this.subcategory = subcategory;
        this.visibility = visibilty;
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getStatus() {
        return status;
    }
}
