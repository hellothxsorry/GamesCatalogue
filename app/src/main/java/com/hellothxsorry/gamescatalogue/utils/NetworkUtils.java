package com.hellothxsorry.gamescatalogue.utils;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class NetworkUtils {
    private static final String BASE_URL_LIST = "https://api.rawg.io/api/games";
    private static final String BASE_URL_DETAILS = "https://api.rawg.io/api/games/%s";
    private static final String BASE_URL_TRAILERS = "https://api.rawg.io/api/games/%s/movies";
    private static final String BASE_URL_REVIEWS = "https://api.rawg.io/api/games/%s/reviews";
    private static final String PARAMS_DATES = "dates";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_SORT_BY = "ordering";
    private static final String PARAMS_PAGE_SIZE = "page_size";

    private static final String SORT_BY_RATING = "-rating:";
    private static final String PAGE_SIZE_NUMBER = "10";

    public static final int POPULAR_NOW = 0;
    public static final int COMING_SOON = 1;

    public static URL buildURLToReviews(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon().build();
        Log.i("Test", uri.toString());
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildURLToVideos(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_TRAILERS, id)).buildUpon().build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildURLToDetails(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_DETAILS, id)).buildUpon().build();
        Log.i("Test", uri.toString());
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildURL(int sortBy, int page) {
        URL result = null;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(currentTime);
        String dateRange;
        if (sortBy == COMING_SOON) {
            dateRange = formattedDate + ",2021-12-31";
        } else {
            dateRange = "2019-01-01," + formattedDate;
        }
        Uri uri = Uri.parse(BASE_URL_LIST).buildUpon()
                .appendQueryParameter(PARAMS_DATES, dateRange)
                .appendQueryParameter(PARAMS_SORT_BY, SORT_BY_RATING)
                .appendQueryParameter(PARAMS_PAGE_SIZE, PAGE_SIZE_NUMBER)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();
        try {
            result = new URL(uri.toString().replace("%2C", ","));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i("Test", result.toString());
        return result;
    }

    public static JSONObject getJSONForReviews(int id) {
        JSONObject result = null;
        URL url = buildURLToReviews(id);
        try {
            result = new JSONDownloadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForVideos(int id) {
        JSONObject result = null;
        URL url = buildURLToVideos(id);
        try {
            result = new JSONDownloadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForDetails(int id) {
        JSONObject result = null;
        URL url = buildURLToDetails(id);
        try {
            result = new JSONDownloadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject result = null;
        URL url = buildURL(sortBy, page);
        Log.i("MyResult", url.toString());
        try {
            result = new JSONDownloadTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private onStartLoadingListener onStartLoadingListener;

        public interface onStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(JSONLoader.onStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            if (url == null) {
                return null;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

    private static class JSONDownloadTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return result;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }
}
