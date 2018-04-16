package com.example.android.filmesfamosos;

/**
 * Created by Romulo on 16/04/2018.
 */

public class Movie {

    public String mTitle;
    public String mImgUrl;
    public String mOverview;
    public double mVoteAverage;
    public String mReleaseDate;


    public Movie(String title, String imgUrl, String overview, double voteAverage, String releaseDate){
        this.mTitle = title;
        this.mOverview = overview;
        this.mImgUrl = imgUrl;
        this.mReleaseDate = releaseDate;
        this.mVoteAverage = voteAverage;
    }
}
