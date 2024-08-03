package com.example.deiteu.model;

import java.util.List;

public class Post {
    private String id;
    private String status;
    private String image;
    private String video;
    private long timecreated;
    private String idUser;

    private int sumLove,sumComments;
    private Users userPost;
    private List<Comments> commentsList;
    private List<Interact> interactList;

    public Post() {
    }
    public Post(String id, String status, String image, long timecreated, String idUser) {
        this.id = id;
        this.status = status;
        this.image = image;
        this.timecreated = timecreated;
        this.idUser = idUser;
    }

    public Post(String id, String status, String image, long timecreated, String idUser, Users userPost) {
        this.id = id;
        this.status = status;
        this.image = image;
        this.timecreated = timecreated;
        this.idUser = idUser;
        this.userPost = userPost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public long getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(long timecreated) {
        this.timecreated = timecreated;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users getUserPost() {
        return userPost;
    }

    public void setUserPost(Users userPost) {
        this.userPost = userPost;
    }

    public int getSumLove() {
        return sumLove;
    }

    public void setSumLove(int sumLove) {
        this.sumLove = sumLove;
    }

    public int getSumComments() {
        return sumComments;
    }

    public void setSumComments(int sumComments) {
        this.sumComments = sumComments;
    }

    public List<Comments> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    public List<Interact> getInteractList() {
        return interactList;
    }

    public void setInteractList(List<Interact> interactList) {
        this.interactList = interactList;
    }
}
