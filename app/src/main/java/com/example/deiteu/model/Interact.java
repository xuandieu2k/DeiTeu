package com.example.deiteu.model;

public class Interact {
    private String id;
    private String idUser;
    private String idPost;
    private String timecreated;

    public Interact() {
    }

    public Interact(String id, String idUser, String idPost, String timecreated) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
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

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(String timecreated) {
        this.timecreated = timecreated;
    }
}
