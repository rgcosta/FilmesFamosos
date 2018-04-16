package com.example.android.filmesfamosos;

/**
 * Created by Romulo on 16/04/2018.
 */


import java.io.IOException;

import android.net.Uri;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    static String MOVIES_BASE_URL =
            "http://api.themoviedb.org/3";

    final static String API_KEY = "api_key";
    final static String key = "b6e57ef91d7c501bb8a54b450f695d97";
    final static String POPULAR_PATH = "/movie/popular";
    final static String RATED_PATH = "/movie/top_rated";


    public static URL buildUrl(boolean isPopular) {
        if (isPopular)
            MOVIES_BASE_URL = MOVIES_BASE_URL + POPULAR_PATH;
        else
            MOVIES_BASE_URL = MOVIES_BASE_URL + RATED_PATH;

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
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

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
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
}