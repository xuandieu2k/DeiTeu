package com.example.deiteu.model;

public class Users {
    Message finalMessage;
    private String id;
    private String fullname;
    private String birthday;
    private String avatar;
    private String gender;
    private String username;
    private String password;
    private String email;
    private String description;
    private String background;

    private boolean online;
    private long timelastonline;
    private String joindate;

    private String idLocation;

    private String[] idFavorite;
    public Users() {
    }

    public Users(String fullname) {
        this.fullname = fullname;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String email, String description, String background, boolean online, long timelastonline, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.email = email;
        this.description = description;
        this.background = background;
        this.online = online;
        this.timelastonline = timelastonline;
        this.joindate = joindate;
    }


    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String description, String background, long timelastonline, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = description;
        this.background = background;
        this.timelastonline = timelastonline;
        this.joindate = joindate;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String description, String background, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = description;
        this.background = background;
        this.joindate = joindate;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String background) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.background = background;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String description, String background, String joindate, String idLocation, String[] idFavorite) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = description;
        this.background = background;
        this.joindate = joindate;
        this.idLocation = idLocation;
        this.idFavorite = idFavorite;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String background, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.background = background;
        this.joindate = joindate;
    }

    public Users(String id, String fullname, String birthday, String avatar, String gender, String username, String password, String email, String description, String background, long timelastonline, String joindate, String idLocation) {
        this.id = id;
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = description;
        this.background = background;
        this.timelastonline = timelastonline;
        this.joindate = joindate;
        this.idLocation = idLocation;
    }

    public Users(String id, String fullname, String email, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.joindate = joindate;
    }

    public Users(String id, String fullname, String email, boolean online, long timelastonline, String joindate) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.online = online;
        this.timelastonline = timelastonline;
        this.joindate = joindate;
    }

    public Users(String fullname, String birthday, String avatar, String gender, String email) {
        this.fullname = fullname;
        this.birthday = birthday;
        this.avatar = avatar;
        this.gender = gender;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getJoindate() {
        return joindate;
    }

    public void setJoindate(String joindate) {
        this.joindate = joindate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(String idLocation) {
        this.idLocation = idLocation;
    }

    public String[] getIdFavorite() {
        return idFavorite;
    }

    public void setIdFavorite(String[] idFavorite) {
        this.idFavorite = idFavorite;
    }

    public long getTimelastonline() {
        return timelastonline;
    }

    public void setTimelastonline(long timelastonline) {
        this.timelastonline = timelastonline;
    }

    public Message getFinalMessage() {
        return finalMessage;
    }

    public void setFinalMessage(Message finalMessage) {
        this.finalMessage = finalMessage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
