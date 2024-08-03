package com.example.deiteu.model;

public class Favorites {
    private String id;
    private String favorite;

    public Favorites() {
    }

    public Favorites(String id, String favorite) {
        this.id = id;
        this.favorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
