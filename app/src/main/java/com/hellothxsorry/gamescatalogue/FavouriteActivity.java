package com.hellothxsorry.gamescatalogue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hellothxsorry.gamescatalogue.adapters.GameAdapter;
import com.hellothxsorry.gamescatalogue.data.FavouriteGame;
import com.hellothxsorry.gamescatalogue.data.Game;
import com.hellothxsorry.gamescatalogue.data.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavourite;
    private GameAdapter adapter;
    private MainViewModel viewModel;
    private LoadingDialog loadingDialog;

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreatePanelMenu(featureId, menu);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        loadingDialog.dissmissLoadingDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        String title = "Favourite Games Library";
        setTitle(title);
        loadingDialog = new LoadingDialog(FavouriteActivity.this);
        loadingDialog.dissmissLoadingDialog();
        recyclerViewFavourite = findViewById(R.id.recycleViewFavourite);
        recyclerViewFavourite.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GameAdapter();
        recyclerViewFavourite.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<FavouriteGame>> favouriteGames = viewModel.getFavouriteGames();
        favouriteGames.observe(this, new Observer<List<FavouriteGame>>() {
            @Override
            public void onChanged(List<FavouriteGame> favouriteGames) {
                List<Game> games = new ArrayList<>();
                if (favouriteGames != null) {
                    games.addAll(favouriteGames);
                }
                adapter.setGames(games);
            }
        });
        adapter.setOnPosterClickListener(new GameAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Game game = adapter.getGames().get(position);
                Intent intent = new Intent(FavouriteActivity.this, DetailedActivity.class);
                intent.putExtra("id", game.getId());
                startActivity(intent);
            }
        });
    }
}
