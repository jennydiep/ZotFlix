package edu.uci.ics.fabflixmobile;

public class Movie {
    private String id;
    private String name;
    private int year;
    private String director;
    private String[] stars;
    private String genres;

    public Movie(String id, String name, int year, String director,String[] stars, String genres) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() { return director; }

    public String getGenres() { return genres; }

    public  String[] getStars() { return stars; }
}