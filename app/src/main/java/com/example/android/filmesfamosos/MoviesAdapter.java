package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romulo on 16/04/2018.
 *
 */

//TODO 1: Terminar design da tela de detalhes do filme

    //TODO 2: Implementar tela de landscape da pagina de detalhes
    //TODO 3: Implementar touch selector
    //TODO 4: Implementar cores padroes
    //TODO 5: revisar se ainda há implementações de desgin.


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    List<Movie> mMovies = new ArrayList<>();

    MoviesOnClickHandler mClickHandler;

    public interface MoviesOnClickHandler {
        void onClick(Movie simpleMovie);
    }


    public MoviesAdapter(MoviesOnClickHandler onClickHandler){
        this.mClickHandler = onClickHandler;
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
                .load(mMovies.get(position).getFullPosterPath())
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;
        else
            return mMovies.size();
    }

    public void setMoviesData(List<Movie> movies){
        this.mMovies = movies;
        notifyDataSetChanged();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;

        public MovieViewHolder(View itemView){
            super(itemView);
            this.mImageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movieClicked = mMovies.get(adapterPosition);
            mClickHandler.onClick(movieClicked);
        }
    }
}
