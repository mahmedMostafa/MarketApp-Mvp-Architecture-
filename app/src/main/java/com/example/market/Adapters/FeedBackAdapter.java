package com.example.market.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.pojos.Review;
import com.example.market.R;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.FeedBackViewHolder> {

    private ArrayList<Review> reviews;
    private Context context;

    public FeedBackAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedBackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item,parent,false);
        return new FeedBackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackViewHolder holder, int position) {
        Review currentReview = reviews.get(position);
        holder.titleTextView.setText(currentReview.getTitle());
        holder.descriptionTextView.setText(currentReview.getDescription());
        holder.buyerTextView.setText("by " +currentReview.getBuyer());
        holder.currentTimeTextView.setText(currentReview.getTimeStamp());
        holder.materialRatingBar.setRating(Float.parseFloat(String.valueOf(currentReview.getRating())));
    }


    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class FeedBackViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView buyerTextView;
        public TextView currentTimeTextView;
        public MaterialRatingBar materialRatingBar;

        public FeedBackViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.review_title);
            descriptionTextView = itemView.findViewById(R.id.review_description);
            buyerTextView = itemView.findViewById(R.id.review_buyer);
            currentTimeTextView = itemView.findViewById(R.id.currentTime_text_view);
            materialRatingBar = itemView.findViewById(R.id.rating_bar);

            // wow, i can't believe that i won't implement a click removeListener :D
        }
    }
}
