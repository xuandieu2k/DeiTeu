package com.example.deiteu.model;

public class Love {
    private String id;
    private String idUser;
    private String timeCreated;

    public Love() {
    }

    public Love(String id, String idUser, String timeCreated) {
        this.id = id;
        this.idUser = idUser;
        this.timeCreated = timeCreated;
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

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }
}
