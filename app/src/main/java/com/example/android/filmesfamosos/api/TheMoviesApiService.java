package com.example.android.filmesfamosos.api;

import com.example.android.filmesfamosos.models.MoviesList;
import com.example.android.filmesfamosos.models.ReviewsList;
import com.example.android.filmesfamosos.models.TrailersList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.example.android.filmesfamosos.utils.NetworkUtils.API_KEY;
import static com.example.android.filmesfamosos.utils.NetworkUtils.MOVIE_TRAILERS;
import static com.example.android.filmesfamosos.utils.NetworkUtils.MOVIE_REVIEWS;
import static com.example.android.filmesfamosos.utils.NetworkUtils.PAGE;
import static com.example.android.filmesfamosos.utils.NetworkUtils.POPULAR_PATH;
import static com.example.android.filmesfamosos.utils.NetworkUtils.RATED_PATH;

public interface TheMoviesApiService {

    @GET(POPULAR_PATH)
    Call<MoviesList> getPopuparMovies(@Query(API_KEY) String key, @Query(PAGE) int page);

    @GET(RATED_PATH)
    Call<MoviesList> getTopRatedMovies(@Query(API_KEY) String key, @Query(PAGE) int page);

    @GET(MOVIE_TRAILERS)    // endpoint: movie/{id}/videos
    Call<TrailersList> getMovieTrailers(@Path("id") int id, @Query(API_KEY) String key);

    @GET(MOVIE_REVIEWS)     // endpoint: movie/{id}/reviews
    Call<ReviewsList> getMovieReviews(@Path("id") int id, @Query(API_KEY) String key);

}
