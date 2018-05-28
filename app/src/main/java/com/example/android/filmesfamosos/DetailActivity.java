package com.example.android.filmesfamosos;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.filmesfamosos.data.MovieContract;
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

    private static final String TAG = DetailActivity.class.getSimpleName();

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

    private int movieId;
    private String posterPath;


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
                this.posterPath = movieDetailed.getPosterPath();    //in order to save into db.
                Picasso.with(this).load(movieDetailed.getFullPosterPath()).into(mMoviePoster);
                this.mOverview.setText(movieDetailed.getOverview());
                this.mReleaseDate.setText(movieDetailed.getReleaseDate());
                this.mVoteAverage.setText(String.valueOf(movieDetailed.getVoteAverage()));
                this.movieId = movieDetailed.getId();   //in order to save into db
                loadTrailers(movieDetailed.getId());
                loadReviews(movieDetailed.getId());
            }
        }
    }

    private void loadTrailers(int id){

        if (isOnline(this)) {
            mLoadingTrailersIndicator.setVisibility(View.VISIBLE);
        } else {
            mReviewAdapter.setReviewsData(null);
        }

        Call<TrailersList> call = new NetworkUtils().getTheMoviesApiService().getMovieTrailers(id, key);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                mLoadingTrailersIndicator.setVisibility(View.INVISIBLE);
                Log.e("TRAILER 1: ", call.request().url().toString());

                if (response.isSuccessful()) {  //evita erro 502 bad server
                    TrailersList trailerListObj = response.body();
                    if (trailerListObj.getTrailers().size() > 0) {
                        mTrailerAdapter.setTrailersData(trailerListObj.getTrailers());
                    }
                } else {
                    Log.e(TAG, String.valueOf(response.code()));
                }

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
            mReviewAdapter.setReviewsData(null);
        }

        Call<ReviewsList> call = new NetworkUtils().getTheMoviesApiService().getMovieReviews(id, key);

        call.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                mLoadingReviewsIndicator.setVisibility(View.INVISIBLE);
                Log.e("REVIEW 1: ", call.request().url().toString() + " - " + response.code() );

                if (response.isSuccessful()) {  //evita erro 502 bad server
                    ReviewsList trailerListObj = response.body();
                    if (trailerListObj.getReviews().size() > 0) {
                        mReviewAdapter.setReviewsData(trailerListObj.getReviews());
                    } else {
                        Log.e(TAG, String.valueOf(response.code()));
                    }
                }
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

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Cursor cursor = getContentResolver().query(MovieContract.FavEntry.CONTENT_URI,
                null,
                "movie_id=?",
                new String[]{String.valueOf(movieId)},
                null);

        if (cursor.moveToFirst()){
            MenuItem menuItem = menu.findItem(R.id.favorite);
            menuItem.setChecked(true);
            menuItem.setIcon(R.drawable.ic_favorite_red_24dp);
            return true;
        }

        return super.onPrepareOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        if (itemSelected == R.id.favorite){

            item.setChecked(!item.isChecked());
            if (item.isChecked()){
                addMovieToFavorite();
                item.setIcon(R.drawable.ic_favorite_red_24dp);
                return true;
            } else {
                removeMovieFromFavorite();
                item.setIcon(R.drawable.ic_favorite_border_red_24dp);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeMovieFromFavorite() {

        String stringMovieId = Integer.toString(movieId);
        Uri uri = MovieContract.FavEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringMovieId).build();

        int moviesRemoved = getContentResolver().delete(uri, null, null);

        if (moviesRemoved > 0) {
            Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
        }

    }

    private void addMovieToFavorite() {
        //Retrieve info from the UI
        String title = mTitle.getText().toString();
        String overview = mOverview.getText().toString();
        String releaseDate = mReleaseDate.getText().toString();
        double voteAverage = Double.parseDouble(mVoteAverage.getText().toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieContract.FavEntry.COLUMN_TITLE, title);
        contentValues.put(MovieContract.FavEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MovieContract.FavEntry.COLUMN_OVERVIEW, overview);
        contentValues.put(MovieContract.FavEntry.COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(MovieContract.FavEntry.COLUMN_VOTE_AVERAGE, voteAverage);

        Uri uri = getContentResolver().insert(MovieContract.FavEntry.CONTENT_URI, contentValues);

        if (uri != null){
            Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
        }
    }

}
