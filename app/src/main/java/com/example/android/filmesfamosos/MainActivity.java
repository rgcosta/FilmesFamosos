package com.example.android.filmesfamosos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Movie[] myMoviesDataSet;

    private MoviesAdapter mAdapter;
    private RecyclerView mMoviesGrid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mMoviesGrid = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMoviesGrid.setLayoutManager(layoutManager);

        mMoviesGrid.setHasFixedSize(true);

        this.mAdapter = new MoviesAdapter();
        mMoviesGrid.setAdapter(mAdapter);

        loadMoviesData(true);
    }

    private void loadMoviesData(boolean isPopular){

        FetchMoviesTask fetchTask = new FetchMoviesTask();
        fetchTask.execute(isPopular);
    }


    public class FetchMoviesTask extends AsyncTask<Boolean, Void, Movie[]> {

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

                //TODO: Format response into movie model
                Log.e("JSON RESPONSE: ", jsonMoviesResponse);


                return new Movie[0];

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mAdapter.setMoviesData(movies);
        }
    }
}
