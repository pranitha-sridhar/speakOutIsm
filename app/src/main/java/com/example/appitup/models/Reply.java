package com.example.appitup.models;

import java.io.Serializable;
import java.util.Map;

public class Reply implements Serializable {

    String reply_id;

    String sent_from;

    String conversation_id;

    String message;
    Map timeStampMap;

    boolean isDeleted = false;

    public Reply(String reply_id, String sent_from, String conversation_id, String message) {
        this.reply_id = reply_id;
        this.sent_from = sent_from;
        this.conversation_id = conversation_id;
        this.message = message;
    }

    public Reply(String reply_id, String sent_from, String conversation_id, String message,Map timeStampMap) {
        this.reply_id = reply_id;
        this.sent_from = sent_from;
        this.conversation_id = conversation_id;
        this.message = message;
        this.timeStampMap=timeStampMap;
    }


    public Map getTimeStampMap() {
        return timeStampMap;
    }



    public void setTimeStampMap(Map timeStampMap) {
        this.timeStampMap = timeStampMap;
    }



    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getSent_from() {
        return sent_from;
    }

    public void setSent_from(String sent_from) {
        this.sent_from = sent_from;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
