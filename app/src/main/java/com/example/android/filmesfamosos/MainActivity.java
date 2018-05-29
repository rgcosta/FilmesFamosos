package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.filmesfamosos.adapters.FavMoviesAdapter;
import com.example.android.filmesfamosos.adapters.MoviesAdapter;
import com.example.android.filmesfamosos.data.MovieContract;
import com.example.android.filmesfamosos.models.Movie;
import com.example.android.filmesfamosos.models.MoviesList;
import com.example.android.filmesfamosos.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.filmesfamosos.utils.NetworkUtils.*;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>,
        FavMoviesAdapter.FavMovieOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMG_SIZE_PIXEL = 441;  //441x662px for w780

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesGrid;

    private TextView mErrorDisplay;
    private ProgressBar mLoadingIndicator;

    private boolean isTopRated;
    private boolean isInFavorite;
    private static final String IS_TOP_RATED_KEY = "is_top_rated_key";
    private static final String IS_IN_FAVORITE_KEY = "is_in_favorite_key";
    private static final String LIST_POSITION_KEY = "list_position_key";

    private static final int FAV_LOADER_ID = 0;

    private Parcelable mScrollState;

    private FavMoviesAdapter mFavMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mErrorDisplay = findViewById(R.id.tv_error_display);
        this.mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        this.mMoviesGrid = findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, getNumberOfColumns());
        mMoviesGrid.setLayoutManager(layoutManager);
        mMoviesGrid.setHasFixedSize(true);

        this.mAdapter = new MoviesAdapter(this);
        this.mFavMovieAdapter = new FavMoviesAdapter(this, this);

        if (savedInstanceState != null){
            this.isTopRated = savedInstanceState.getBoolean(IS_TOP_RATED_KEY);
            this.isInFavorite = savedInstanceState.getBoolean(IS_IN_FAVORITE_KEY);
            if (savedInstanceState.containsKey(LIST_POSITION_KEY)){
                this.mScrollState = savedInstanceState.getParcelable(LIST_POSITION_KEY);
            }
        }

        if (isInFavorite){
            mMoviesGrid.setAdapter(mFavMovieAdapter);
            getSupportLoaderManager().restartLoader(FAV_LOADER_ID, null, this);
        } else {
            loadMoviesData(isTopRated);
        }

    }

    private int getNumberOfColumns() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        Log.i(TAG, "widthPixels  = " + widthPixels);

        return widthPixels/IMG_SIZE_PIXEL;
    }

    private void loadMoviesData(boolean isTopRated){

        if (isOnline(this)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = MainActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        if (mMoviesGrid.getAdapter() == null){
            mMoviesGrid.setAdapter(mAdapter);
        } else if (mMoviesGrid.getAdapter().getClass() != MoviesAdapter.class) {
            mMoviesGrid.setAdapter(mAdapter);
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
                //MoviesList movies = response.body();
                MoviesList moviesData = response.body();
                Log.e("STAGE 1: ", call.request().url().toString());

                showMoviesGrid();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mAdapter.setMoviesData(moviesData.getMovies());
                mMoviesGrid.getLayoutManager().onRestoreInstanceState(mScrollState);
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
    public void onFavMovieClick(Movie singleMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, singleMovie);
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

        switch (menuItemSelected){
            case (R.id.popular):
                mAdapter.setMoviesData(null);
                loadMoviesData(false);
                this.isTopRated = false;
                this.isInFavorite = false;
                return true;
            case (R.id.top_rated):
                mAdapter.setMoviesData(null);
                loadMoviesData(true);
                this.isTopRated = true;
                this.isInFavorite = false;
                return true;
            case R.id.favorites:
                mMoviesGrid.setAdapter(mFavMovieAdapter);
                getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);
                this.isInFavorite = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_TOP_RATED_KEY, isTopRated);
        savedInstanceState.putBoolean(IS_IN_FAVORITE_KEY, isInFavorite);
        savedInstanceState.putParcelable(LIST_POSITION_KEY, mMoviesGrid.getLayoutManager().onSaveInstanceState());

        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavMovies = null;

            @Override
            protected void onStartLoading() {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                if (mFavMovies != null) {
                    deliverResult(mFavMovies);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(
                            MovieContract.FavEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.FavEntry._ID);
                } catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mFavMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mFavMovieAdapter.swapCursor(data);
        mMoviesGrid.getLayoutManager().onRestoreInstanceState(mScrollState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mFavMovieAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAV_LOADER_ID, null, this);
    }

}
