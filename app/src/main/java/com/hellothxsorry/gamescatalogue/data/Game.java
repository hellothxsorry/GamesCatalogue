package com.hellothxsorry.gamescatalogue.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class Game {
    @PrimaryKey(autoGenerate = true)
    private int uniqueId;
    private int id;
    private String name;
    private String posterLink;
    private String releaseDate;

    public Game(int uniqueId, int id, String name, String posterLink, String releaseDate) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.name = name;
        this.posterLink = posterLink;
        this.releaseDate = releaseDate;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Ignore
    public Game(int id, String name, String posterLink, String releaseDate) {
        this.id = id;
        this.name = name;
        this.posterLink = posterLink;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterLink() {
        return posterLink;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
