package com.example.android.filmesfamosos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    List<Trailer> mTrailers = new ArrayList<>();

    TrailersOnClickHandler mClickHandler;

    public interface TrailersOnClickHandler {
        void onClick(Trailer singleTrailer);
    }

    public TrailersAdapter(TrailersOnClickHandler onClickHandler){
        this.mClickHandler = onClickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Picasso.with(holder.mImageViewTrailer.getContext())
                .load(mTrailers.get(position).getYoutubeCoverImgFullUrl())
                .into(holder.mImageViewTrailer);
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null)
            return 0;
        else
            return mTrailers.size();
    }

    public void setTrailersData(List<Trailer> Trailers) {
        this.mTrailers = Trailers;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mImageViewTrailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            this.mImageViewTrailer = itemView.findViewById(R.id.imageView_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int trailerClickedPosition = getAdapterPosition();
            Trailer trailerClicked = mTrailers.get(trailerClickedPosition);
            mClickHandler.onClick(trailerClicked);
        }
    }
}
