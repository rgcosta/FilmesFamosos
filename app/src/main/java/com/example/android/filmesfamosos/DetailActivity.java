package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.example.android.filmesfamosos.NetworkUtils.*;
import static com.example.android.filmesfamosos.NetworkUtils.isOnline;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersOnClickHandler,
        ReviewsAdapter.ReviewsOnClickHandler {

    private TextView mTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private ImageView mMoviePoster;
    private ProgressBar mLoadingTrailersIndicator;
    private ProgressBar mLoadingReviewsIndicator;

    private TrailersAdapter mTrailerAdapter;
    private RecyclerView mTrailerDisplay;

    private ReviewsAdapter mReviewAdapter;
    private RecyclerView mReviewDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.mTitle = findViewById(R.id.tv_title);
        this.mOverview = findViewById(R.id.tv_overview);
        this.mReleaseDate = findViewById(R.id.tv_release_date);
        this.mVoteAverage = findViewById(R.id.tv_rate_average);
        this.mMoviePoster = findViewById(R.id.iv_movie_poster);
        this.mLoadingTrailersIndicator = findViewById(R.id.pb_loading_trailers_indicator);
        this.mTrailerDisplay = findViewById(R.id.rv_trailers);
        this.mLoadingReviewsIndicator = findViewById(R.id.pb_loading_reviews_indicator);
        this.mReviewDisplay = findViewById(R.id.rv_reviews);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, HORIZONTAL, false);
        mTrailerDisplay.setLayoutManager(trailerLayoutManager);
        mTrailerDisplay.setHasFixedSize(true);

        this.mTrailerAdapter = new TrailersAdapter(this);
        mTrailerDisplay.setAdapter(mTrailerAdapter);


        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, VERTICAL, false);
        mReviewDisplay.setLayoutManager(reviewLayoutManager);
        mReviewDisplay.setHasFixedSize(true);

        this.mReviewAdapter =  new ReviewsAdapter(this);
        mReviewDisplay.setAdapter(mReviewAdapter);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                Movie movieDetailed = intent.getParcelableExtra(Intent.EXTRA_TEXT);

                this.mTitle.setText(movieDetailed.getTitle());
                Picasso.with(this).load(movieDetailed.getFullPosterPath()).into(mMoviePoster);
                this.mOverview.setText(movieDetailed.getOverview());
                this.mReleaseDate.setText(movieDetailed.getReleaseDate());
                this.mVoteAverage.setText(String.valueOf(movieDetailed.getVoteAverage()));
                loadTrailers(movieDetailed.getId());
                loadReviews(movieDetailed.getId());
            }
        }
    }

    private void loadTrailers(int id){

        if (isOnline(this)) {
            mLoadingTrailersIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = DetailActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        Call<TrailersList> call = new NetworkUtils().getTheMoviesApiService().getMovieTrailers(id, key);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                mLoadingTrailersIndicator.setVisibility(View.INVISIBLE);
                Log.e("TRAILER 1: ", call.request().url().toString());

                TrailersList trailerListObj = response.body();
                if (trailerListObj.getTrailers().size() > 0)
                    mTrailerAdapter.setTrailersData(trailerListObj.getTrailers());
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Log.e("Can't load trailers: ", t.getMessage());
            }
        });
    }


    private void loadReviews(int id){

        if (isOnline(this)) {
            mLoadingReviewsIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = DetailActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        Call<ReviewsList> call = new NetworkUtils().getTheMoviesApiService().getMovieReviews(id, key);

        call.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                mLoadingReviewsIndicator.setVisibility(View.INVISIBLE);
                Log.e("REVIEW 1: ", call.request().url().toString());

                ReviewsList trailerListObj = response.body();
                if (trailerListObj.getReviews().size() > 0)
                    mReviewAdapter.setReviewsData(trailerListObj.getReviews());
            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Log.e("Can't load reviews: ", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(Trailer singleTrailer) {
        Intent openTrailerIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + singleTrailer.getKey()) );
        startActivity(openTrailerIntent);
    }

    @Override
    public void onClick(Review singleReview) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(singleReview.getContent())
                .setTitle(singleReview.getAuthor());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
