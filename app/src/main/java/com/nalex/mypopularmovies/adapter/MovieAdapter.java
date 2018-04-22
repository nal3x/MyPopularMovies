package com.nalex.mypopularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<Movie> mMovieList;
    private final Context mContext;
    final private MovieAdapterOnClickHandler mClickHandler;
    final private MovieAdapterOnLongClickHandler mLongClickHandler;

    //The interface that receives onClick messages
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public interface MovieAdapterOnLongClickHandler {
        void onLongClick(Movie movie);
    }

    public MovieAdapter(@NonNull Context context,
                        MovieAdapterOnClickHandler clickHandler,
                        MovieAdapterOnLongClickHandler longClickHandler,
                        ArrayList<Movie> movieList) {
        mContext = context;
        mClickHandler = clickHandler;
        mLongClickHandler = longClickHandler;
        this.mMovieList = movieList;

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bindPoster(position);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final ImageView movieItemImageView;
        final View movieSelectedOverlay;

        MovieViewHolder (View itemView) {
            super(itemView);
            movieItemImageView = itemView.findViewById(R.id.iv_movie_poster);
            movieSelectedOverlay = itemView.findViewById(R.id.selected_overlay);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bindPoster (int index) {
            Movie movie = mMovieList.get(index);
            String relativePath = movie.getPosterPath();
            URL imageURL = NetworkUtils.buildImageUrl(relativePath);
            if (null != imageURL.toString())
                Picasso.get().load(imageURL.toString()).into(movieItemImageView);
            if (movie.isSelected()) {
                movieSelectedOverlay.setVisibility(View.VISIBLE);
            } else
                movieSelectedOverlay.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }

        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieList.get(adapterPosition);
            mLongClickHandler.onLongClick(selectedMovie);
            notifyDataSetChanged();
            return true;
        }
    }
}
