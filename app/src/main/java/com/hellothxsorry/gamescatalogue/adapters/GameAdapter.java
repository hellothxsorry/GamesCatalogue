package com.hellothxsorry.gamescatalogue.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hellothxsorry.gamescatalogue.LoadingDialog;
import com.hellothxsorry.gamescatalogue.MainActivity;
import com.hellothxsorry.gamescatalogue.R;
import com.hellothxsorry.gamescatalogue.data.Game;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> games;
    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public GameAdapter() {
        games = new ArrayList<>();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GameViewHolder holder, int position) {
        if (games.size() >= 8 && position == games.size() - 3 && onReachEndListener != null) {
            onReachEndListener.onReachEnd();
        }
        final Game game = games.get(position);
        Picasso.get().load(game.getPosterLink()).fit().centerCrop().placeholder(R.drawable.placeholder).into(holder.imageViewSmallPoster);
        holder.textViewGameTitleLabel.setText(game.getName());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSmallPoster;
        private TextView textViewGameTitleLabel;


        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
            textViewGameTitleLabel = itemView.findViewById(R.id.textViewGameTitleLabel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void clear() {
        this.games.clear();
        notifyDataSetChanged();
    }

    public void setGames(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();

    }

    public void addGames(List<Game> games) {
        this.games.addAll(games);
        notifyDataSetChanged();
    }

    public List<Game> getGames() {
        return games;
    }
}
