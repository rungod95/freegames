package com.svalero.freegames.model;

public class Game {

    private int id;
    private String title;
    private String thumbnail;
    private String short_description;
    private String game_url;
    private String genre;
    private String platform;
    private String publisher;
    private String developer;
    private String release_date;

    // Getters y setters

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getGame_url() {
        return game_url;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getRelease_date() {
        return release_date;
    }



    @Override
    public String toString() {
        return title + " (" + platform + ")";
    }

}
