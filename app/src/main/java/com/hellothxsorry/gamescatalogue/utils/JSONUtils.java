package com.hellothxsorry.gamescatalogue.utils;

import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.util.Log;

import com.hellothxsorry.gamescatalogue.LoadingDialog;
import com.hellothxsorry.gamescatalogue.MainActivity;
import com.hellothxsorry.gamescatalogue.data.Game;
import com.hellothxsorry.gamescatalogue.data.Information;
import com.hellothxsorry.gamescatalogue.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    private static final String KEY_RESULTS = "results";

    //for the games list
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_THUMB_POSTER_LINK = "background_image";
    private static final String KEY_RELEASE_DATE = "released";

    //game details
    private static final String KEY_METACRITIC = "metacritic";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_USERS_RATING = "rating";


    //users' reviews
    private static final String KEY_TRAILER_PREVIEW = "preview";
    private static final String KEY_USER = "user";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REVIEW = "text";
    private static final String KEY_USER__REVIEW_SCORE = "rating";

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1Review = jsonArray.getJSONObject(i);
                JSONObject jsonObjectUsername = jsonObject1Review.getJSONObject(KEY_USER);
                String username = jsonObjectUsername.getString(KEY_USERNAME) + ":";
                String text = jsonObject1Review.getString(KEY_REVIEW);

                Review review = new Review(username, text);
                result.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Information> getGameInformationFromJSON(JSONObject jsonObject) {
        ArrayList<Information> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectDescription = jsonArray.getJSONObject(i);
                String description = jsonObjectDescription.getString(KEY_DESCRIPTION);
                int metacritic = jsonObjectDescription.getInt(KEY_METACRITIC);
                String released = jsonObjectDescription.getString(KEY_RELEASE_DATE);

                Information information = new Information(description, metacritic, released);
                result.add(information);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Game> getGamesFromJSON(JSONObject jsonObject) {
        ArrayList<Game> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectGame = jsonArray.getJSONObject(i);
                int id = objectGame.getInt(KEY_ID);
                String name = objectGame.getString(KEY_NAME);
                String thumbPosterLink = objectGame.getString(KEY_THUMB_POSTER_LINK);
                String releaseDate = objectGame.getString(KEY_RELEASE_DATE);
                Game game = new Game(id, name, thumbPosterLink, releaseDate);
                result.add(game);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
