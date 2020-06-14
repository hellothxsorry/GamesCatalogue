package com.hellothxsorry.gamescatalogue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hellothxsorry.gamescatalogue.adapters.GameAdapter;
import com.hellothxsorry.gamescatalogue.adapters.ReviewAdapter;
import com.hellothxsorry.gamescatalogue.data.FavouriteGame;
import com.hellothxsorry.gamescatalogue.data.Game;
import com.hellothxsorry.gamescatalogue.data.MainViewModel;
import com.hellothxsorry.gamescatalogue.data.Review;
import com.hellothxsorry.gamescatalogue.utils.JSONUtils;
import com.hellothxsorry.gamescatalogue.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private ImageView imageViewPoster;
    private TextView textViewName;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private ImageView imageViewAddToFavourite;
    private TextView textViewDescription;
    private RecyclerView recyclerViewReviewText;
    private ReviewAdapter reviewAdapter;
    private TextView textViewLabelComments;

    private int id;
    private MainViewModel viewModel;
    private Game game;
    private FavouriteGame favouriteGame;
    private GameAdapter adapter;
    private LoadingDialog loadingDialog;
    private ScrollView scrollViewInfo;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        loadingDialog = new LoadingDialog(DetailedActivity.this);
        loadingDialog.dissmissLoadingDialog();
        adapter = new GameAdapter();
        imageViewPoster = findViewById(R.id.imageViewPoster);
        textViewName = findViewById(R.id.textViewName);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        textViewDescription = findViewById(R.id.textViewDecription);
        textViewLabelComments = findViewById(R.id.textViewLabelComments);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        game = viewModel.getGameById(id);
        Picasso.get().load(game.getPosterLink()).fit().centerCrop().placeholder(R.drawable.placeholder).into(imageViewPoster);
        textViewName.setText(game.getName());
        textViewReleaseDate.setText(game.getReleaseDate());
        setFavourite();
        textViewDescription = findViewById(R.id.textViewDecription);
        recyclerViewReviewText = findViewById(R.id.recycleViewReviews);
        reviewAdapter = new ReviewAdapter();
        recyclerViewReviewText.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviewText.setAdapter(reviewAdapter);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(game.getId());
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        if (reviews.isEmpty()) {
            textViewLabelComments.setText(R.string.no_comments);
        }
        JSONObject jsonObjectDetailedInformation = NetworkUtils.getJSONForDetails(game.getId());
        try {
            String description = jsonObjectDetailedInformation.getString("description");
            Integer metacriticScore = jsonObjectDetailedInformation.getInt("metacritic");
            String score = Integer.toString(metacriticScore) + "/100";
            textViewRating.setText(score);
            textViewDescription.setText(Html.fromHtml(description));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadingDialog.dissmissLoadingDialog();
        adapter.notifyDataSetChanged();
    }

    public void onClickChangeFavourite(View view) {

        if (favouriteGame == null) {
            viewModel.insertFavouriteGame(new FavouriteGame(game));
            Toast.makeText(this, "Added to the Favourite", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteGame(favouriteGame);
            Toast.makeText(this, "Removed from the Favourite", Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite() {
        favouriteGame = viewModel.getFavouriteGameById(id);
        if (favouriteGame == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.empty);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.added);
        }
    }
}
