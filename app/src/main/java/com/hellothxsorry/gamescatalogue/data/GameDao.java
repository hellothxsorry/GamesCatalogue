package com.hellothxsorry.gamescatalogue.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GameDao {
    @Query("SELECT * FROM games")
    LiveData<List<Game>> getAllGames();

    @Query("SELECT * FROM favourite_games")
    LiveData<List<FavouriteGame>> getAllFavouriteGames();

    @Query("SELECT * FROM games WHERE id == :gameId")
    Game getGameById(int gameId);

    @Query("SELECT * FROM favourite_games WHERE id == :gameId")
    FavouriteGame getFavouriteGameById(int gameId);

    @Query("DELETE FROM games")
    void deleteAllGames();

    @Insert
    void insertGame(Game game);

    @Delete
    void deleteGame(Game game);

    @Insert
    void insertFavouriteGame(FavouriteGame game);

    @Delete
    void deleteFavouriteGame(FavouriteGame game);
}
