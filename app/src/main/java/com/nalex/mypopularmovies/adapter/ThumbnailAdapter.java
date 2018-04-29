package com.nalex.mypopularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.MovieVideoResult;
import com.nalex.mypopularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    private ArrayList<MovieVideoResult> mVideosList;
    private Context mContext;

    public ThumbnailAdapter(@NonNull Context context,
                          ArrayList<MovieVideoResult> mVideosList) {
        mContext = context;
        this.mVideosList = mVideosList;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.thumbnail_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ThumbnailViewHolder viewHolder = new ThumbnailViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        String site = mVideosList.get(position).getSite();
        if (site.equalsIgnoreCase("youtube")){
            String videoKey = mVideosList.get(position).getKey();
            URL url = NetworkUtils.buildYoutubeThumbnailUrl(videoKey);
            if (null != url.toString())
                Picasso.get().load(url.toString()).into(holder.thumbnailImageView);
        }
    }

    @Override
    public int getItemCount() {
        int youtubeVideosCount = 0;
        for (MovieVideoResult res : mVideosList) {
            if (res.getSite().equalsIgnoreCase("youtube"));
            youtubeVideosCount++;
        }
        return youtubeVideosCount;
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        final ImageView thumbnailImageView;

        public ThumbnailViewHolder(View itemView) {
            super(itemView);
            this.thumbnailImageView = itemView.findViewById(R.id.thumbnail_imageview);
        }
    }
}
