package com.nalex.mypopularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.MovieReviewResult;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ArrayList<MovieReviewResult> mMovieReviewResults;
    private final Context mContext;

    public ReviewsAdapter(@NonNull Context context, ArrayList<MovieReviewResult> movieReviewResults) {
        mContext = context;
        mMovieReviewResults = movieReviewResults;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        String author = mContext.getString(R.string.review_by) + mMovieReviewResults.get(position).getAuthor();
        holder.authorTextView.setText(author);
        holder.reviewTextView.setText(mMovieReviewResults.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mMovieReviewResults) return 0;
        return mMovieReviewResults.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView authorTextView;
        final TextView reviewTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.author_textview);
            reviewTextView = itemView.findViewById(R.id.review_textview);
        }


    }
}
