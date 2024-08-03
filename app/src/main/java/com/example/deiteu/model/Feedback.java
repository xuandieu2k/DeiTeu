package com.example.deiteu.model;

public class Feedback {
    private String id;
    private String idUser;
    private String feedback;
    private Long timecreated;


    public Feedback() {
    }

    public Feedback(String id, String idUser, String feedback, Long timecreated) {
        this.id = id;
        this.idUser = idUser;
        this.feedback = feedback;
        this.timecreated = timecreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Long getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Long timecreated) {
        this.timecreated = timecreated;
    }
}
