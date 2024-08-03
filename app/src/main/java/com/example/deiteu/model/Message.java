package com.example.deiteu.model;

public class Message {
    private String msgId;
    private String senderId;
    private String message;
    private String image;
    private long timeSend;
    private boolean readed;

    public Message(String msgId, String senderId, String message, long timeSend) {
        this.msgId = msgId;
        this.senderId = senderId;
        this.message = message;
        this.timeSend = timeSend;
    }


    public Message(String msgId, String senderId, String message, String image, long timeSend) {
        this.msgId = msgId;
        this.senderId = senderId;
        this.message = message;
        this.image = image;
        this.timeSend = timeSend;
    }

    public Message() {
    }

    public Message(String msgId, String senderId, String message, String image, long timeSend, boolean readed) {
        this.msgId = msgId;
        this.senderId = senderId;
        this.message = message;
        this.image = image;
        this.timeSend = timeSend;
        this.readed = readed;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
