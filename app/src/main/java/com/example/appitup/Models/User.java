package com.example.appitup.Models;

public class User {
    String username;

    String email;

    String displayName;

    String profileUri;

    String uid;

    public User(String username, String email, String displayName, String profileUri, String uid) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.profileUri = profileUri;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public String getUid() {
        return uid;
    }
}
