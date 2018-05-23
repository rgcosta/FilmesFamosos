package com.example.android.filmesfamosos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoviesList {

    @Expose @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

   // @Expose
   // private Movie movie;

    public void setMovies(List<Movie> movies){
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }
    /*
    public void setMovie(Movie movie){
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }
    */
}
