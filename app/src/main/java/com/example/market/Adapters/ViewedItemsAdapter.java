package com.example.market.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.database.ViewedItem;
import com.example.market.R;

import java.util.ArrayList;
import java.util.List;

public class ViewedItemsAdapter extends RecyclerView.Adapter<ViewedItemsAdapter.ViewedItemsViewHolder> {

    List<ViewedItem> allItems = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public ViewedItemsAdapter(Context context){
        this.context = context;
    }


    public interface OnItemClickListener{
        void onItemClick(String label ,String id);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recently_viewed_item,parent,false);
        return new ViewedItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewedItemsViewHolder holder, int position) {
        ViewedItem currentItem = allItems.get(position);
        Glide.with(context)
                .load(currentItem.getImageUrl()).into(holder.imageView);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.priceTextView.setText(currentItem.getPrice());
        holder.oldPriceTextView.setText(currentItem.getOldPrice());
        holder.brandTextView.setText(currentItem.getBrand());
        if(holder.discountTextView.equals("")){
            holder.discountTextView.setVisibility(View.INVISIBLE);
        }else{
            holder.discountTextView.setText(currentItem.getDiscount());
        }
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }


    public void setViewedItemsList(List<ViewedItem> list){
        this.allItems = list;
        //VIP
        notifyDataSetChanged();
    }

    public ViewedItem getViewedItemAt(int position){
        return allItems.get(position);
    }

    public class ViewedItemsViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView brandTextView;
        public TextView titleTextView;
        public TextView priceTextView;
        public TextView oldPriceTextView;
        public TextView discountTextView;
        public Button buyNowButton;

        public ViewedItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.viewed_image);
            brandTextView = itemView.findViewById(R.id.viewed_brand);
            titleTextView = itemView.findViewById(R.id.viewed_title);
            priceTextView = itemView.findViewById(R.id.viewed_price);
            oldPriceTextView = itemView.findViewById(R.id.viewed_old_price);
            discountTextView = itemView.findViewById(R.id.viewed_discount);
            buyNowButton = itemView.findViewById(R.id.buy_now_button);

         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(listener != null){
                     int position = getAdapterPosition();
                     listener.onItemClick(allItems.get(position).getCategory(),allItems.get(position).getItem());
                 }
             }
         });
        }
    }
}
