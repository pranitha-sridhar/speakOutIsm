package com.example.appitup.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Complaints implements Serializable {
    String complaintId;
    String username;
    String uid;
    String subject;
    String body;
    String category;
    String subcategory;
    String visibility;
    String status;
    String anonymous;
    ArrayList<String> listOfUpVoter;
    ArrayList<String> listOfDownVoter;
    ArrayList<Comment> listOfCommenter;

    public Complaints(String complaintId, String username, String uid, String subject, String body, String category, String subcategory, String visibilty, String status, String anonymous) {
        this.complaintId = complaintId;
        this.username = username;
        this.uid = uid;
        this.subject = subject;
        this.body = body;
        this.category = category;
        this.subcategory = subcategory;
        this.visibility = visibilty;
        this.status = status;
        this.anonymous = anonymous;
    }

    public Complaints(String complaintId, String username, String uid, String subject, String body, String category, String subcategory, String visibility, String status, String anonymous, ArrayList<String> listOfUpVoter, ArrayList<String> listOfDownVoter, ArrayList<Comment> listOfCommenter) {
        this.complaintId = complaintId;
        this.username = username;
        this.uid = uid;
        this.subject = subject;
        this.body = body;
        this.category = category;
        this.subcategory = subcategory;
        this.visibility = visibility;
        this.status = status;
        this.anonymous = anonymous;
        this.listOfUpVoter = listOfUpVoter;
        this.listOfDownVoter = listOfDownVoter;
        this.listOfCommenter = listOfCommenter;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public ArrayList<String> getListOfUpVoter() {
        return listOfUpVoter;
    }

    public void setListOfUpVoter(ArrayList<String> listOfUpVoter) {
        this.listOfUpVoter = listOfUpVoter;
    }

    public ArrayList<String> getListOfDownVoter() {
        return listOfDownVoter;
    }

    public void setListOfDownVoter(ArrayList<String> listOfDownVoter) {
        this.listOfDownVoter = listOfDownVoter;
    }

    public ArrayList<Comment> getListOfCommenter() {
        return listOfCommenter;
    }

    public void setListOfCommenter(ArrayList<Comment> listOfCommenter) {
        this.listOfCommenter = listOfCommenter;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
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
