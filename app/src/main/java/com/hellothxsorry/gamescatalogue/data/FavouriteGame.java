package com.hellothxsorry.gamescatalogue.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_games")
public class FavouriteGame extends Game {
    public FavouriteGame(int uniqueId, int id, String name, String posterLink, String releaseDate) {
        super(uniqueId, id, name, posterLink, releaseDate);
    }

    @Ignore
    public FavouriteGame(Game game) {
        super(game.getUniqueId(), game.getId(), game.getName(), game.getPosterLink(), game.getReleaseDate());
    }
}
