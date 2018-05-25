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

    @Expose
    private int page;

    @Expose @SerializedName("total_pages")
    private int totalPages;

    public void setMovies(List<Movie> movies){
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
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
