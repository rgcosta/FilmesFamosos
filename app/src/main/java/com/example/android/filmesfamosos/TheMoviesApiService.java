package com.example.android.filmesfamosos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.example.android.filmesfamosos.NetworkUtils.API_KEY;
import static com.example.android.filmesfamosos.NetworkUtils.POPULAR_PATH;
import static com.example.android.filmesfamosos.NetworkUtils.RATED_PATH;

public interface TheMoviesApiService {

    @GET(POPULAR_PATH)
    Call<MoviesList> getPopuparMovies(@Query(API_KEY) String key);

    @GET(RATED_PATH)
    Call<MoviesList> getTopRatedMovies(@Query(API_KEY) String key);

}
