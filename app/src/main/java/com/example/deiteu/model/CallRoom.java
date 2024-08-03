package com.example.deiteu.model;

public class CallRoom {
    private String id;
    private String idCaller;
    private String idReceiver;
    private boolean calling;
    private boolean callingVideo;
    private boolean acceptCall;
    private boolean refuseCall; // Tu choi
    private boolean end;

    public CallRoom() {
    }

    public CallRoom(String id, String idCaller, String idReceiver, boolean calling, boolean callingVideo) {
        this.id = id;
        this.idCaller = idCaller;
        this.idReceiver = idReceiver;
        this.calling = calling;
        this.callingVideo = callingVideo;
    }

    public CallRoom(String id, String idCaller, String idReceiver, boolean calling, boolean callingVideo, boolean acceptCall, boolean refuseCall, boolean end) {
        this.id = id;
        this.idCaller = idCaller;
        this.idReceiver = idReceiver;
        this.calling = calling;
        this.callingVideo = callingVideo;
        this.acceptCall = acceptCall;
        this.refuseCall = refuseCall;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCaller() {
        return idCaller;
    }

    public void setIdCaller(String idCaller) {
        this.idCaller = idCaller;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public boolean isCalling() {
        return calling;
    }

    public void setCalling(boolean calling) {
        this.calling = calling;
    }

    public boolean isCallingVideo() {
        return callingVideo;
    }

    public void setCallingVideo(boolean callingVideo) {
        this.callingVideo = callingVideo;
    }

    public boolean isAcceptCall() {
        return acceptCall;
    }

    public void setAcceptCall(boolean acceptCall) {
        this.acceptCall = acceptCall;
    }

    public boolean isRefuseCall() {
        return refuseCall;
    }

    public void setRefuseCall(boolean refuseCall) {
        this.refuseCall = refuseCall;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
