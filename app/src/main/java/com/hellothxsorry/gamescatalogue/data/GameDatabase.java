package com.hellothxsorry.gamescatalogue.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Game.class, FavouriteGame.class}, version = 1, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {

    private static final String DB_NAME = "games.db";
    private static GameDatabase database;
    private static final Object LOCK = new Object();

    static GameDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, GameDatabase.class, DB_NAME).build();
            }
            return database;
        }
    }

    public abstract GameDao gameDao();
}
