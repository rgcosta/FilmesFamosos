package com.example.android.filmesfamosos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> mReviews = new ArrayList<>();

    private ReviewsOnClickHandler mClickHandler;

    public interface ReviewsOnClickHandler {
        void onClick(Review singleReview);
    }

    public ReviewsAdapter(ReviewsOnClickHandler onClickHandler){
        this.mClickHandler = onClickHandler;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.mReviewName.setText(mReviews.get(position).getAuthor());
        holder.mReviewContent.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews == null)
            return 0;
        else
            return mReviews.size();
    }

    public void setReviewsData(List<Review> reviews){
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mReviewName;
        TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            this.mReviewName = itemView.findViewById(R.id.tv_review_name);
            this.mReviewContent = itemView.findViewById(R.id.tv_review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int reviewClickedPosition = getAdapterPosition();
            Review reviewClicked = mReviews.get(reviewClickedPosition);
            mClickHandler.onClick(reviewClicked);
        }
    }
}
