package com.example.appitup.models;

public class User {
    String username;

    String email;

    String displayName;

    String profileUri;

    String uid;

    int userType;

    public User(String username, String email, String displayName, String profileUri, String uid) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.profileUri = profileUri;
        this.uid = uid;
    }

    public User(String username, String email, String displayName, String profileUri, String uid, int userType) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.profileUri = profileUri;
        this.uid = uid;
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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
