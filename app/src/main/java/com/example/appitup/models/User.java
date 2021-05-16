package com.example.appitup.models;

import java.io.Serializable;

public class User implements Serializable {
    String username;

    String email;

    String displayName;

    String profileUri;

    String uid;

    int userType;

    boolean isBlocked = false;

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
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
