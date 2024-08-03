package com.example.deiteu.model;

public class Follows {
    private String id;
    private String idFollower;
    private String timeFollow;

    public Follows(String id, String idFollower, String timeFollow) {
        this.id = id;
        this.idFollower = idFollower;
        this.timeFollow = timeFollow;
    }

    public Follows() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFollower() {
        return idFollower;
    }

    public void setIdFollower(String idFollower) {
        this.idFollower = idFollower;
    }

    public String getTimeFollow() {
        return timeFollow;
    }

    public void setTimeFollow(String timeFollow) {
        this.timeFollow = timeFollow;
    }
}
