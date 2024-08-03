package com.example.deiteu.model;

public class Comments {

    private Users userComnent;
    private String id;
    private String idUser;
    private String idPost;
    private String content;

    private String image;
    private String timecreated;

    public Comments() {
    }

    public Comments(String id, String idUser, String idPost, String content, String timecreated) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.content = content;
        this.timecreated = timecreated;
    }

    public Comments(Users userComnent, String id, String idUser, String idPost, String content, String image, String timecreated) {
        this.userComnent = userComnent;
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.content = content;
        this.image = image;
        this.timecreated = timecreated;
    }

    public Comments(String id, String idUser, String idPost, String content, String image, String timecreated) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.content = content;
        this.image = image;
        this.timecreated = timecreated;
    }

    public Users getUserComnent() {
        return userComnent;
    }

    public void setUserComnent(Users userComnent) {
        this.userComnent = userComnent;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(String timecreated) {
        this.timecreated = timecreated;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
