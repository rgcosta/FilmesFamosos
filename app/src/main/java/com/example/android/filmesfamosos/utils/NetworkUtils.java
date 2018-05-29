package com.example.android.filmesfamosos.utils;

/**
 * Created by Romulo on 16/04/2018.
 */


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.filmesfamosos.BuildConfig;
import com.example.android.filmesfamosos.api.TheMoviesApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkUtils {

    public final static String MOVIES_BASE_URL =
            "http://api.themoviedb.org/3/";     //Terminar com / para não causar Exception no Retrofit2
    public final static String POSTER_BASE_URL =
            "http://image.tmdb.org/t/p";
    public final static String IMG_SIZE = "/w780/";    //options: "w92", "w154", "w185", "w342", "w500", "w780" ou "original"

    public final static String API_KEY = "api_key";
    public final static String key = BuildConfig.key;
    public final static String POPULAR_PATH = "movie/popular";
    public final static String RATED_PATH = "movie/top_rated";
    public final static String MOVIE_TRAILERS = "movie/{id}/videos";
    public final static String MOVIE_REVIEWS = "movie/{id}/reviews";

    private final Retrofit retrofit;

    public NetworkUtils(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl(MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public TheMoviesApiService getTheMoviesApiService(){
        return this.retrofit.create(TheMoviesApiService.class);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /* Functions were removed after implementation of Retrofit library
    **
    ** Left just for studies purposes.
    *

    public static URL buildUrl(boolean isPopular) {
        if (isPopular)
            MOVIES_URL = MOVIES_BASE_URL + POPULAR_PATH;
        else
            MOVIES_URL = MOVIES_BASE_URL + RATED_PATH;

        Uri builtUri = Uri.parse(MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY, key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Movie[] getMoviesFromJson(String jsonString) throws JSONException {

        final String RESULTS_ARRAY = "results";
        final String TITLE = "original_title";
        final String POSTER = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        //final String IMG_SIZE = "/w780/";    //options: "w92", "w154", "w185", "w342", "w500", "w780" ou "original"

        JSONObject moviesJsonObj = new JSONObject(jsonString);
        JSONArray moviesArray = moviesJsonObj.getJSONArray(RESULTS_ARRAY);

        Movie[] moviesData = new Movie[moviesArray.length()];
        for(int i=0; i < moviesArray.length(); i++){
            JSONObject simpleMovie = moviesArray.getJSONObject(i);

            String title = simpleMovie.getString(TITLE);
            String imgUrl = "http://image.tmdb.org/t/p" + IMG_SIZE + simpleMovie.getString(POSTER);
            String overview = simpleMovie.getString(OVERVIEW);
            Double voteAverage = simpleMovie.getDouble(VOTE_AVERAGE);
            String releaseDate = simpleMovie.getString(RELEASE_DATE);

            moviesData[i] = new Movie(title,imgUrl,overview, voteAverage, releaseDate);
        }

        return moviesData;
    }
    */
}