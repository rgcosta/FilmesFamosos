package com.example.android.filmesfamosos;

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

public class MainActivity extends AppCompatActivity {

    private Movie[] myMoviesDataSet;

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesGrid;

    private TextView mErrorDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mErrorDisplay = (TextView) findViewById(R.id.tv_error_display);
        this.mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        this.mMoviesGrid = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMoviesGrid.setLayoutManager(layoutManager);

        mMoviesGrid.setHasFixedSize(true);

        this.mAdapter = new MoviesAdapter();
        mMoviesGrid.setAdapter(mAdapter);

        loadMoviesData(true);
    }

    private void loadMoviesData(boolean isPopular){
        showMoviesGrid();

        FetchMoviesTask fetchTask = new FetchMoviesTask();
        fetchTask.execute(isPopular);
    }

    private void showMoviesGrid(){
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMoviesGrid.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mMoviesGrid.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
    }


    public class FetchMoviesTask extends AsyncTask<Boolean, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(Boolean... booleans) {

            /* If there's no booleans, there's nothing to look up. */
            if (booleans.length == 0) {
                return null;
            }

            boolean isPopular = booleans[0];
            URL moviesRequestUrl = NetworkUtils.buildUrl(isPopular);
            Log.e("BASE URL: ", moviesRequestUrl.toString());

            try {
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

                Log.e("JSON RESPONSE: ", jsonMoviesResponse);
                Movie[] moviesData = NetworkUtils.getMoviesFromJson(jsonMoviesResponse);

                return moviesData;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null) {
                showMoviesGrid();
                mAdapter.setMoviesData(movies);
            } else {
                showErrorMessage();
            }

        }
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
