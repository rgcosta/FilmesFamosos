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

import static com.example.android.filmesfamosos.NetworkUtils.*;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesOnClickHandler {

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesGrid;

    private TextView mErrorDisplay;
    private ProgressBar mLoadingIndicator;

    private boolean isTopRated;
    private static final String IS_TOP_RATED_KEY = "is_top_rated_key";

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

        if (savedInstanceState != null){
            this.isTopRated = savedInstanceState.getBoolean(IS_TOP_RATED_KEY);
        }
        loadMoviesData(isTopRated);


    }

    private void loadMoviesData(boolean isTopRated){

        if (isOnline(this)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = MainActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        Call<MoviesList> call;
        if (isTopRated){
            call = new NetworkUtils().getTheMoviesApiService().getTopRatedMovies(key);
        } else {
            call = new NetworkUtils().getTheMoviesApiService().getPopuparMovies(key);

        }

        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                MoviesList movies = response.body();
                Log.e("STAGE 1: ", call.request().url().toString());

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
        startActivity(intent);
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
            loadMoviesData(false);
            this.isTopRated = false;
            return true;
        }
        else if (menuItemSelected == R.id.top_rated){
            mAdapter.setMoviesData(null);
            loadMoviesData(true);
            this.isTopRated = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_TOP_RATED_KEY, isTopRated);
        super.onSaveInstanceState(savedInstanceState);
    }
}
