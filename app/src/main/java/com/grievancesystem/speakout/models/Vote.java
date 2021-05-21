package com.grievancesystem.speakout.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Vote implements Serializable {
    @SerializedName("complaint_id")
    String complaint_id;

    @SerializedName("username")
    String username;

    public Vote() {
    }

    public Vote(String complaint_id, String username) {
        this.complaint_id = complaint_id;
        this.username = username;
    }

    public String getComplaint_id() {
        return complaint_id;
    }

    public void setComplaint_id(String complaint_id) {
        this.complaint_id = complaint_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
