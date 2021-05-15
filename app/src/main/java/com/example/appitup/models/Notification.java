package com.example.appitup.models;

import java.util.Map;

public class Notification {
    String title;
    String message;
    Map<String,String> data;
    Map timeStampMap;

    public Notification(String title, String message, Map<String, String> data) {
        this.title = title;
        this.message = message;
        this.data = data;
    }

    public Notification(String title, String message, Map<String, String> data, Map timeStampMap) {
        this.title = title;
        this.message = message;
        this.data = data;
        this.timeStampMap=timeStampMap;
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

    public String getMessage() {
        return message;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
