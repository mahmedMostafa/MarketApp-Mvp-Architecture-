package com.example.market.favourites;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.pojos.FavouriteItem;
import com.example.market.R;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder> {

    private Context context;
    private List<FavouriteItem> favouriteItems;
    private onCloseButtonClickListener listener;
    private onItemClickListener itemClickListener;

    public interface onCloseButtonClickListener{
        void onItemClick(int position);
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setOnCloseButtonClickListener(onCloseButtonClickListener listener){
        this.listener = listener;
    }

    public FavouritesAdapter(Context context, List<FavouriteItem> favouriteItems) {
        this.context = context;
        this.favouriteItems = favouriteItems;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourites_item,parent,false);
        return new FavouritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {
        FavouriteItem currentItem = favouriteItems.get(position);
        Glide.with(context).load(currentItem.getImageUrl()).into(holder.imageView);
        holder.brandTextView.setText(currentItem.getBrand());
        holder.titleTextView.setText(currentItem.getTitle());
        holder.priceTextView.setText(currentItem.getPrice());
        if(!(currentItem.getOldPrice().equals("none"))){
            holder.oldPriceTextView.setVisibility(View.VISIBLE);
            holder.oldPriceTextView.setText(currentItem.getOldPrice());
            holder.oldPriceTextView.setPaintFlags(holder.oldPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if(!(currentItem.getDiscount().equals(""))){
            holder.discountTextView.setVisibility(View.VISIBLE);
            holder.discountTextView.setText(currentItem.getDiscount());
        }
    }

    public void setFavouriteItems(List<FavouriteItem> items){
        this.favouriteItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return favouriteItems.size();
    }

    public class FavouritesViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView titleTextView;
        public TextView priceTextView;
        public TextView oldPriceTextView;
        public TextView discountTextView;
        public TextView brandTextView;
        public Button buyNowButton;
        public Button deleteFavouriteButton;

        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.liked_image);
            titleTextView = itemView.findViewById(R.id.liked_title);
            priceTextView = itemView.findViewById(R.id.liked_price);
            oldPriceTextView = itemView.findViewById(R.id.liked_oldPrice);
            discountTextView = itemView.findViewById(R.id.liked_discount);
            brandTextView = itemView.findViewById(R.id.liked_brand);
            buyNowButton = itemView.findViewById(R.id.liked_buy_now_button);
            deleteFavouriteButton = itemView.findViewById(R.id.delete_favourite_button);

            deleteFavouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
