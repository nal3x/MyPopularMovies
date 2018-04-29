package com.nalex.mypopularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.MovieReviewResult;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    //TODO: set padding programmaticaly so when review is expanded padding is correct

    private ArrayList<MovieReviewResult> mMovieReviewResults;
    private final Context mContext;
    // sparse boolean array for checking the state of the review(expanded, shrinked)
    private SparseBooleanArray reviewStateArray = new SparseBooleanArray();

    public ReviewsAdapter(@NonNull Context context,
                          ArrayList<MovieReviewResult> movieReviewResults) {
        mContext = context;
        mMovieReviewResults = movieReviewResults;
    }

    public interface ReviewsAdapterOnClickHandler {
        void onClick(MovieReviewResult reviewResult);
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
        String author = mContext.getString(R.string.review_by) + " " + mMovieReviewResults.get(position).getAuthor();
        holder.authorTextView.setText(author);
        holder.reviewTextView.setText(mMovieReviewResults.get(position).getContent());

        if (!reviewStateArray.get(position, false)) {
            holder.reviewTextView.setMaxLines(4);
            holder.readMoreTextView.setVisibility(View.VISIBLE);
        }
        else {
            holder.reviewTextView.setMaxLines(Integer.MAX_VALUE);
            holder.readMoreTextView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (null == mMovieReviewResults) return 0;
        return mMovieReviewResults.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView authorTextView;
        final TextView reviewTextView;
        final TextView readMoreTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.author_textview);
            reviewTextView = itemView.findViewById(R.id.review_textview);
            readMoreTextView = itemView.findViewById(R.id.review_read_more_tv);
            reviewTextView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!reviewStateArray.get(adapterPosition, false)) {
                reviewStateArray.put(adapterPosition, true);
                reviewTextView.setMaxLines(Integer.MAX_VALUE);//Expanded
                readMoreTextView.setVisibility(View.INVISIBLE);
            }
            else  {
                reviewStateArray.put(adapterPosition, false); //Shrinked
                reviewTextView.setMaxLines(4);
                readMoreTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
