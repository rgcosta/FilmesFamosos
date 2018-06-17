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
import android.widget.Toast;

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

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.filmesfamosos.utils.NetworkUtils.*;

//TODO 1: make recyclerview scrolls smoothly
//TODO 2: ao adicionar mais filmes ao adapter, posiçõ do scroll volta para o inicio. Corrigir!!

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>,
        FavMoviesAdapter.FavMovieOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int IMG_SIZE_PIXEL = 441;  //441x662px for w780

    private MoviesAdapter mAdapter;
    private FavMoviesAdapter mFavMovieAdapter;
    private RecyclerView mMoviesGrid;

    private TextView mErrorDisplay;
    private ProgressBar mLoadingIndicator;

    private static final String IS_TOP_RATED_KEY = "is_top_rated_key";
    private static final String SCROLL_POSITION_KEY = "scroll_position_key";
    private static final String PAGE_POPULAR_KEY = "page_popular_key";
    private static final String TOTAL_PAGES_POPULAR_KEY = "total_pages_popular_key";
    private static final String PAGE_TOP_RATED_KEY = "page_top_rated_key";
    private static final String TOTAL_PAGES_TOP_RATED_KEY = "total_pages_top_rated_key";
    private static final String POPULAR_MOVIES_LIST_KEY = "popular_movies_list_key";
    private static final String TOP_RATED_MOVIES_LIST_KEY = "top_rated_movies_list_key";
    private static final String SWAP_MODE_KEY = "swap_mode_key";

    private static final int IS_FAVORITE_ID = 1;
    private static final int FETCH_NEW_DATA_ID = 2;
    private static final int REUSING_TOP_RATED_MOVIES_ID = 3;
    private static final int REUSING_POPULAR_MOVIES_ID = 4;

    private int swapMode = FETCH_NEW_DATA_ID;
    private boolean isTopRated;


    private static final int FAV_LOADER_ID = 0;

    private Parcelable mScrollState;
    private List<Movie> mMoviesPopular;
    private List<Movie> mMoviesTopRated;
    private int pagePopular = 1;
    private int totalPagesPopular;
    private int pageTopRated = 1;
    private int totalPagesTopRated;


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
            this.mScrollState = savedInstanceState.getParcelable(SCROLL_POSITION_KEY);
            this.swapMode = savedInstanceState.getInt(SWAP_MODE_KEY);
            this.pagePopular = savedInstanceState.getInt(PAGE_POPULAR_KEY);
            this.pageTopRated = savedInstanceState.getInt(PAGE_TOP_RATED_KEY);
            this.totalPagesPopular = savedInstanceState.getInt(TOTAL_PAGES_POPULAR_KEY);
            this.totalPagesTopRated = savedInstanceState.getInt(TOTAL_PAGES_TOP_RATED_KEY);
            if (savedInstanceState.containsKey(POPULAR_MOVIES_LIST_KEY)){
                this.mMoviesPopular = savedInstanceState.getParcelableArrayList(POPULAR_MOVIES_LIST_KEY);
            }
            if (savedInstanceState.containsKey(TOP_RATED_MOVIES_LIST_KEY)){
                this.mMoviesTopRated = savedInstanceState.getParcelableArrayList(TOP_RATED_MOVIES_LIST_KEY);
            }
        }

        mMoviesGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /* This callback (onScrolled) will also be called if visible item range changes after a
            ** layout calculation. In that case, dx and dy will be 0.
            ** That is the reason wht I used (dx+dy)!=0!!
            * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.OnScrollListener#onScrollStateChanged(android.support.v7.widget.RecyclerView,%20int)
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(View.FOCUS_DOWN) && (dx+dy)!=0 &&
                        swapMode != IS_FAVORITE_ID) {
                    Log.e(TAG, "onScrollStateChanged: BOTTOM ");

                    if (isTopRated) {
                        if (pageTopRated < totalPagesTopRated) {
                            pageTopRated++;
                            loadMoviesData(isTopRated);
                        }
                    } else {
                        if (pagePopular < totalPagesPopular) {
                            pagePopular++;
                            loadMoviesData(isTopRated);
                        }
                    }
                }
            }
        });

        loadMovies();
    }

    private void loadMovies() {

        switch (swapMode){
            case IS_FAVORITE_ID:
                mMoviesGrid.setAdapter(mFavMovieAdapter);
                getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);
                break;
            case FETCH_NEW_DATA_ID:
                loadMoviesData(isTopRated);
                break;
            case REUSING_TOP_RATED_MOVIES_ID:
                Log.e(TAG, "REUSING mMoviesTopRated. Savind Data!");
                mMoviesGrid.setAdapter(mAdapter);
                mAdapter.setMoviesData(mMoviesTopRated);
                break;
            case REUSING_POPULAR_MOVIES_ID:
                Log.e(TAG, "REUSING mMoviesPopular. Savind Data!");
                mMoviesGrid.setAdapter(mAdapter);
                mAdapter.setMoviesData(mMoviesPopular);
                break;
            default:
                Log.e(TAG, "No swapMode Selected: " + swapMode);
        }
    }

    private int getNumberOfColumns() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        Log.i(TAG, "widthPixels  = " + widthPixels);

        return widthPixels/IMG_SIZE_PIXEL;
    }

    private void loadMoviesData(final boolean isTopRated){

        if (isOnline(this)) {
            mMoviesGrid.setAdapter(mAdapter);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            Context context = MainActivity.this;
            Intent noConnectionIntent = new Intent(context, NoInternetConnectionActivity.class);
            startActivity(noConnectionIntent);
        }

        Call<MoviesList> call;
        if (isTopRated){
            call = new NetworkUtils().getTheMoviesApiService().getTopRatedMovies(key, pageTopRated);
        } else {
            call = new NetworkUtils().getTheMoviesApiService().getPopuparMovies(key, pagePopular);
        }

        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                Log.e("STAGE 1: ", call.request().url().toString());
                showMoviesGrid();
                mLoadingIndicator.setVisibility(View.INVISIBLE);

                MoviesList moviesData = response.body();
                if (isTopRated) {
                    if (pageTopRated > 1)
                        mMoviesTopRated.addAll(moviesData.getMovies());
                    else
                        mMoviesTopRated = moviesData.getMovies();
                    mAdapter.setMoviesData(mMoviesTopRated);
                    swapMode = REUSING_TOP_RATED_MOVIES_ID;
                    pageTopRated = moviesData.getPage();
                    totalPagesTopRated = moviesData.getTotalPages();
                } else {
                    if (pagePopular > 1)
                        mMoviesPopular.addAll(moviesData.getMovies());
                    else
                        mMoviesPopular = moviesData.getMovies();
                    mAdapter.setMoviesData(mMoviesPopular);
                    swapMode = REUSING_POPULAR_MOVIES_ID;
                    pagePopular = moviesData.getPage();
                    totalPagesPopular = moviesData.getTotalPages();
                }
                Log.e(TAG, "onResponse - Filmes: " + mMoviesGrid.getAdapter().getItemCount());
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
                this.isTopRated = false;
                swapMode = REUSING_POPULAR_MOVIES_ID;
                loadMovies();
                return true;
            case (R.id.top_rated):
                mAdapter.setMoviesData(null);
                if (mMoviesTopRated == null) {
                    swapMode = FETCH_NEW_DATA_ID;
                    this.isTopRated = true;
                    loadMovies();
                    return true;
                }
                this.isTopRated = true;
                swapMode = REUSING_TOP_RATED_MOVIES_ID;
                loadMovies();
                return true;
            case R.id.favorites:
                swapMode = IS_FAVORITE_ID;
                loadMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_TOP_RATED_KEY, isTopRated);
        savedInstanceState.putParcelable(SCROLL_POSITION_KEY, mMoviesGrid.getLayoutManager().onSaveInstanceState());
        savedInstanceState.putInt(SWAP_MODE_KEY, swapMode);
        savedInstanceState.putInt(PAGE_POPULAR_KEY, pagePopular);
        savedInstanceState.putInt(PAGE_TOP_RATED_KEY, pageTopRated);
        savedInstanceState.putInt(TOTAL_PAGES_POPULAR_KEY, totalPagesPopular);
        savedInstanceState.putInt(TOTAL_PAGES_TOP_RATED_KEY, totalPagesTopRated);
        if (mMoviesPopular != null)
            savedInstanceState.putParcelableArrayList(POPULAR_MOVIES_LIST_KEY, (ArrayList<? extends Parcelable>) mMoviesPopular);
        if (mMoviesTopRated != null)
            savedInstanceState.putParcelableArrayList(TOP_RATED_MOVIES_LIST_KEY, (ArrayList<? extends Parcelable>) mMoviesTopRated);

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
