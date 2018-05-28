package com.example.android.filmesfamosos.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.filmesfamosos.utils.NetworkUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Romulo on 16/04/2018.
 */

public class Movie implements Parcelable {

    @Expose
    private int id;

    @Expose
    private String title;

    @Expose @SerializedName("poster_path")
    private String posterPath;

    @Expose
    private String overview;

    @Expose @SerializedName("vote_average")
    private double voteAverage;

    @Expose @SerializedName("release_date")
    private String releaseDate;


    public Movie(int id, String title, String imgUrl, String overview, double voteAverage, String releaseDate){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = imgUrl;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }



    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }

    public String getOverview(){
        return this.overview;
    }

    public void setPosterPath(String posterPath){
        this.posterPath = posterPath;
    }

    public String getPosterPath(){
        return this.posterPath;
    }

    public void setVoteAverage(double voteAverage){
        this.voteAverage = voteAverage;
    }

    public double getVoteAverage(){
        return this.voteAverage;
    }

    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate(){
        return this.releaseDate;
    }

    public String getFullPosterPath(){
        return "http://image.tmdb.org/t/p" + NetworkUtils.IMG_SIZE + getPosterPath();
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }

    @Override
    public String toString() {
        return  "ID: " + this.id + "\n" +
                "Title: " + this.title + "\n" +
                "Overview: " + this.overview + "\n" +
                "Vote: " + this.voteAverage + "\n" +
                "Date: " + this.releaseDate + "\n" +
                "Poster: " + this.posterPath + "\n";
    }
}
