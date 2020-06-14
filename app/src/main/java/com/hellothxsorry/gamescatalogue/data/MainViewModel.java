package com.hellothxsorry.gamescatalogue.data;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    private static GameDatabase database;
    private LiveData<List<Game>> games;
    private LiveData<List<FavouriteGame>> favouriteGames;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = GameDatabase.getInstance(getApplication());
        games = database.gameDao().getAllGames();
        favouriteGames = database.gameDao().getAllFavouriteGames();
    }

    public Game getGameById(int id) {
        try {
            return new GetGameTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavouriteGame getFavouriteGameById(int id) {
        try {
            return new GetFavouriteGameTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<FavouriteGame>> getFavouriteGames() {
        return favouriteGames;
    }

    public void deleteAllGames() {
        new DeleteGamesTask().execute();
    }

    public void insertGame(Game game) {
        new InsertGameTask().execute(game);
    }

    public void deleteGame(Game game) {
        new DeleteGameTask().execute(game);
    }

    public void insertFavouriteGame(FavouriteGame game) {
        new InsertFavouriteGameTask().execute(game);
    }

    public void deleteFavouriteGame(FavouriteGame game) {
        new DeleteFavouriteGameTask().execute(game);
    }

    private static class InsertFavouriteGameTask extends AsyncTask<FavouriteGame, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteGame... games) {
            if (games != null && games.length > 0) {
                database.gameDao().insertFavouriteGame(games[0]);
            }
            return null;
        }
    }

    private static class DeleteFavouriteGameTask extends AsyncTask<FavouriteGame, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteGame... games) {
            if (games != null && games.length > 0) {
                database.gameDao().deleteFavouriteGame(games[0]);
            }
            return null;
        }
    }

    private static class DeleteGameTask extends AsyncTask<Game, Void, Void> {
        @Override
        protected Void doInBackground(Game... games) {
            if (games != null && games.length > 0) {
                database.gameDao().deleteGame(games[0]);
            }
            return null;
        }
    }

    private static class InsertGameTask extends AsyncTask<Game, Void, Void> {
        @Override
        protected Void doInBackground(Game... games) {
            if (games != null && games.length > 0) {
                database.gameDao().insertGame(games[0]);
            }
            return null;
        }
    }

    private static class DeleteGamesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.gameDao().deleteAllGames();
            return null;
        }
    }

    private static class GetGameTask extends AsyncTask<Integer, Void, Game> {
        @Override
        protected Game doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.gameDao().getGameById(integers[0]);
            }
            return null;
        }
    }

    private static class GetFavouriteGameTask extends AsyncTask<Integer, Void, FavouriteGame> {
        @Override
        protected FavouriteGame doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.gameDao().getFavouriteGameById(integers[0]);
            }
            return null;
        }
    }

    public LiveData<List<Game>> getGames() {
        return games;
    }
}
