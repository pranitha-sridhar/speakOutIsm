package com.example.appitup.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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
    long downvotes;
    long upvotes;
    ArrayList<Vote> listOfUpvoters;
    ArrayList<Vote> listOfDownvoters;
    ArrayList<Comment> listOfCommenter;
    Map timeStampmap;
    String timeStampStr;
    int voteStatus;

    public Complaints(String complaintId, String username, String uid, String subject, String body, String category, String subcategory, String visibilty, String status, String anonymous, Map timeStampmap) {
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
        this.timeStampmap = timeStampmap;
    }

    public Complaints(String complaintId, String username, String uid, String subject, String body,
                      String category, String subcategory, String visibility, String status, String anonymous,
                      long upvotes, long downvotes, ArrayList<Comment> listOfCommenter,
                      ArrayList<Vote> listOfUpvoters, ArrayList<Vote> listOfDownvoters,
                      Map timeStampmap, String timeStampStr, int voteStatus) {
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
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.listOfCommenter = listOfCommenter;
        this.listOfUpvoters = listOfUpvoters;
        this.listOfDownvoters = listOfDownvoters;
        this.timeStampmap = timeStampmap;
        this.timeStampStr = timeStampStr;
        this.voteStatus = voteStatus;
    }

    public int getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(int voteStatus) {
        this.voteStatus = voteStatus;
    }

    public ArrayList<Vote> getListOfUpvoters() {
        return listOfUpvoters;
    }

    public void setListOfUpvoters(ArrayList<Vote> listOfUpvoters) {
        this.listOfUpvoters = listOfUpvoters;
    }

    public ArrayList<Vote> getListOfDownvoters() {
        return listOfDownvoters;
    }

    public void setListOfDownvoters(ArrayList<Vote> listOfDownvoters) {
        this.listOfDownvoters = listOfDownvoters;
    }

    public Map getTimeStampmap() {
        return timeStampmap;
    }

    public void setTimeStampmap(Map timeStampmap) {
        this.timeStampmap = timeStampmap;
    }

    public String getTimeStampStr() {
        return timeStampStr;
    }

    public void setTimeStampStr(String timeStampStr) {
        this.timeStampStr = timeStampStr;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
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

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
