package com.hellothxsorry.gamescatalogue.data;

public class Information {
    private String description;
    private int metacritic;
    private String released;

    public Information(String description, int metacritic, String released) {
        this.description = description;
        this.metacritic = metacritic;
        this.released = released;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMetacritic() {
        return metacritic;
    }

    public void setMetacritic(int metacritic) {
        this.metacritic = metacritic;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }
}
