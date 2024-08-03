package com.example.deiteu.model;

public class Notification {
    private Users users;
    private String id;
    private String idPost;
    private String idUser; // User tương tác bài viết
    private String content;
    private String image;
    private long timeCreated;
    private String type; // 1: Thông báo bài viết mới, 2: Thông báo thả tim bài viết, 3: thông báo bình luận bài viết
    private boolean readed;

    private Comments comments;

    public Notification() {
    }

    public Notification(String id, String idPost, String idUser, String content, long timeCreated, String type, boolean readed) {
        this.id = id;
        this.idPost = idPost;
        this.idUser = idUser;
        this.content = content;
        this.timeCreated = timeCreated;
        this.type = type;
        this.readed = readed;
    }

    public Notification(String id, String idPost, String idUser, String content, String image, long timeCreated, String type, boolean readed) {
        this.id = id;
        this.idPost = idPost;
        this.idUser = idUser;
        this.content = content;
        this.image = image;
        this.timeCreated = timeCreated;
        this.type = type;
        this.readed = readed;
    }

    public Notification(Users users, String id, String idPost, String idUser, String content, String image, long timeCreated, String type, boolean readed, Comments comments) {
        this.users = users;
        this.id = id;
        this.idPost = idPost;
        this.idUser = idUser;
        this.content = content;
        this.image = image;
        this.timeCreated = timeCreated;
        this.type = type;
        this.readed = readed;
        this.comments = comments;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }
}
