package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Romulo on 16/04/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    Movie[] mMovies;


    public MoviesAdapter(){
        //this.mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Picasso.with(holder.mImageView.getContext())
                .load(mMovies[position].mImgUrl)
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;
        else
            return mMovies.length;
    }

    public void setMoviesData(Movie[] movies){
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public MovieViewHolder(View itemView){
            super(itemView);
            this.mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
