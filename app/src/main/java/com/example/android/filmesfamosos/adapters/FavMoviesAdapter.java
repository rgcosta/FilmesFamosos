package com.example.android.filmesfamosos.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.data.MovieContract;
import com.example.android.filmesfamosos.models.Movie;
import com.example.android.filmesfamosos.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMovieViewHolder>{

    private Cursor mCursor;
    private Context mContext;

    FavMovieOnClickHandler mClickHandler;

    public interface FavMovieOnClickHandler {
        void onFavMovieClick(Movie singleMovie);
    }

    public FavMoviesAdapter(Context context, FavMovieOnClickHandler onClickHandler){
        this.mContext = context;
        this.mClickHandler = onClickHandler;
    }

    @Override
    public FavMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_grid_item, parent, false);

        return new FavMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavMovieViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int posterPathIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_POSTER_PATH);

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Determine the values of the wanted data
        String posterPath = mCursor.getString(posterPathIndex);

        //Set value
        Picasso.with(mContext)
                .load(NetworkUtils.POSTER_BASE_URL + NetworkUtils.IMG_SIZE + posterPath)
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        else
            return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor newCursor) that is passed in.
     */
    public Cursor swapCursor(Cursor newCursor){
        if (mCursor == newCursor){
            return null; //nothing has changed
        }

        Cursor temp = mCursor;
        this.mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class FavMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;

        public FavMovieViewHolder(View itemView) {
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.imageView_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int favMovieClicked = getAdapterPosition();
            mCursor.moveToPosition(favMovieClicked);

            // Indices for the _id, description, and priority columns
            //int idIndex = mCursor.getColumnIndex(MovieContract.FavEntry._ID);
            int movieIdIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_MOVIE_ID);
            int titleIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_TITLE);
            int posterLinkPath = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_POSTER_PATH);
            int overviewIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_OVERVIEW);
            int releaseDateIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_RELEASE_DATE);
            int voteAverageIndex = mCursor.getColumnIndex(MovieContract.FavEntry.COLUMN_VOTE_AVERAGE);

            // Determine the values of the wanted data
            //final int id = mCursor.getInt(idIndex);
            int movieId = mCursor.getInt(movieIdIndex);
            String title = mCursor.getString(titleIndex);
            String overview = mCursor.getString(overviewIndex);
            String releaseDate = mCursor.getString(releaseDateIndex);
            double voteAverage = mCursor.getDouble(voteAverageIndex);
            String posterPath = mCursor.getString(posterLinkPath);

            Movie movieClicked = new Movie(movieId, title, posterPath, overview, voteAverage, releaseDate);
            mClickHandler.onFavMovieClick(movieClicked);
        }
    }
}
