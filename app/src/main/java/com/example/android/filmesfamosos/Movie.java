package com.example.android.filmesfamosos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Romulo on 16/04/2018.
 */

public class Movie implements Parcelable {

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

    protected Movie(Parcel in) {
        mTitle = in.readString();
        mImgUrl = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mImgUrl);
        parcel.writeString(mOverview);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mReleaseDate);
    }
}
