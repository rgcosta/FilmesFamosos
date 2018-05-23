package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesOnClickHandler {

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesGrid;

    private TextView mErrorDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mErrorDisplay = findViewById(R.id.tv_error_display);
        this.mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        this.mMoviesGrid = findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        int mOrientation = getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager.setSpanCount(4);
        }
        mMoviesGrid.setLayoutManager(layoutManager);

        mMoviesGrid.setHasFixedSize(true);

        this.mAdapter = new MoviesAdapter(this);
        mMoviesGrid.setAdapter(mAdapter);

        loadMoviesData(true);
    }

    private void loadMoviesData(boolean isPopular){
        //showMoviesGrid();

        if (isOnline()) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = MainActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        Call<MoviesList> call;
        if (isPopular){
            call = new NetworkUtils().getTheMoviesApiService().getPopuparMovies(NetworkUtils.key);
        } else {
            call = new NetworkUtils().getTheMoviesApiService().getTopRatedMovies(NetworkUtils.key);
        }

        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                MoviesList movies = response.body();
                Log.e("STAGE 1: ", movies.getMovies().size() + " - " + movies.getMovies().get(0).title);

                showMoviesGrid();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mAdapter.setMoviesData(movies.getMovies());
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                showErrorMessage();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                Log.e("STAGE 3: ", t.getMessage());
            }
        });
    }

    private void showMoviesGrid(){
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMoviesGrid.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mMoviesGrid.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie simpleMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, simpleMovie);
        /*
        intent.putExtra(Intent.EXTRA_TEXT + "_title", simpleMovie.mTitle);
        intent.putExtra(Intent.EXTRA_TEXT + "_poster", simpleMovie.mImgUrl);
        intent.putExtra(Intent.EXTRA_TEXT + "_overview", simpleMovie.mOverview);
        intent.putExtra(Intent.EXTRA_TEXT + "_voteAverage", simpleMovie.mVoteAverage);
        intent.putExtra(Intent.EXTRA_TEXT + "_releaseDate", simpleMovie.mReleaseDate);
        */
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected == R.id.popular){
            mAdapter.setMoviesData(null);
            loadMoviesData(true);
            return true;
        }
        else if (menuItemSelected == R.id.top_rated){
            mAdapter.setMoviesData(null);
            loadMoviesData(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
