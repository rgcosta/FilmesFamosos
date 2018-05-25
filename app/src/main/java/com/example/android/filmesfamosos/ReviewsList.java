package com.example.android.filmesfamosos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewsList {

    @Expose @SerializedName("results")
    private List<Review> reviews = new ArrayList<>();

    @Expose
    private int page;

    @Expose @SerializedName("total_pages")
    private int totalPages;

    public List<Review> getReviews() {
        return reviews;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
