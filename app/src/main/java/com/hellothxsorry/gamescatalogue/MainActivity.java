package com.hellothxsorry.gamescatalogue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hellothxsorry.gamescatalogue.adapters.GameAdapter;
import com.hellothxsorry.gamescatalogue.data.Game;
import com.hellothxsorry.gamescatalogue.data.MainViewModel;
import com.hellothxsorry.gamescatalogue.utils.JSONUtils;
import com.hellothxsorry.gamescatalogue.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private GameAdapter gameAdapter;
    private Switch switchSort;
    private TextView textViewTopRated;
    private TextView textViewReleaseDate;
    private LoadingDialog loadingDialog;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 999;
    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;
    private LoaderManager loaderManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                loadingDialog.startLoadingDialog();
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.itemFavourite:
                loadingDialog.startLoadingDialog();
                Intent intentFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getComlumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics((displayMetrics));
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 220 > 2 ? width / 220 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loaderManager = LoaderManager.getInstance(this); //Singleton pattern
        loadingDialog = new LoadingDialog(MainActivity.this);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        switchSort = findViewById(R.id.switchSort);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        final RecyclerView recyclerViewPosters = findViewById(R.id.recycleViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getComlumnCount()));
        gameAdapter = new GameAdapter();
        recyclerViewPosters.setAdapter(gameAdapter);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
                if (loadingDialog.getAlertDialog() != null) {
                    loadingDialog.dissmissLoadingDialog();
                }
                loadingDialog.startLoadingDialog();
            }
        });
        switchSort.setChecked(false);
        gameAdapter.setOnPosterClickListener(new GameAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Game game = gameAdapter.getGames().get(position);
                loadingDialog.startLoadingDialog();
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("id", game.getId());
                startActivity(intent);
            }
        });
        gameAdapter.setOnReachEndListener(new GameAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    downloadData(methodOfSort, page);
                }
            }
        });
        LiveData<List<Game>> gamesFromLiveData = viewModel.getGames();
        gamesFromLiveData.observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if (page == 1) {
                    gameAdapter.setGames(games);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadingDialog.dissmissLoadingDialog();
    }

    public void onClickSetTopRated(View view) {
        loadingDialog.startLoadingDialog();
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetReleased(View view) {
        loadingDialog.startLoadingDialog();
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isComingSoon) {
        if (isComingSoon) {
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
            textViewReleaseDate.setTextColor(getResources().getColor(R.color.colorAccent));
            methodOfSort = NetworkUtils.COMING_SOON;
        } else {
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewReleaseDate.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.POPULAR_NOW;
        }
        downloadData(methodOfSort, page);
    }

    private void downloadData(int sortMethod, int page) {
        URL url = NetworkUtils.buildURL(sortMethod, page);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, bundle);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.onStartLoadingListener() {
            @Override
            public void onStartLoading() {
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        ArrayList<Game> games = JSONUtils.getGamesFromJSON(jsonObject);
        if (games != null && !games.isEmpty()) {
            if (page == 1) {
                viewModel.deleteAllGames();
                gameAdapter.clear();
            }
            for (Game game : games) {
                viewModel.insertGame(game);
            }
            gameAdapter.addGames(games);
            page++;
        }
        isLoading = false;
        loadingDialog.dissmissLoadingDialog();
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}