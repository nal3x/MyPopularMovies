package com.nalex.mypopularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideos {

    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<MovieVideoResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieVideoResult> getResults() {
        return results;
    }

    public void setResults(List<MovieVideoResult> results) {
        this.results = results;
        }

}
