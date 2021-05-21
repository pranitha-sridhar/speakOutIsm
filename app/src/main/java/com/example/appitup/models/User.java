package com.example.appitup.models;

import java.io.Serializable;

public class User implements Serializable {
    String username;

    String email;

    String displayName;

    String profileUri;

    String uid;

    String fcm_token;

    int userType;

    boolean isBlocked = false;

    boolean isLoggedIn = false;

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

    public User(String username, String email, String displayName, String profileUri, String uid, int userType, boolean isBlocked) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.profileUri = profileUri;
        this.uid = uid;
        this.userType = userType;
        this.isBlocked = isBlocked;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
