package com.example.market.home.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.market.pojos.BestProductsItem;
import com.example.market.R;

import java.util.ArrayList;

public class BestProductsAdapter extends RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder> {

    private Context context;
    private ArrayList<BestProductsItem> bestProductsItems;
    private OnItemClickListener listener;

    public BestProductsAdapter(Context context, ArrayList<BestProductsItem> bestProductsItems) {
        this.context = context;
        this.bestProductsItems = bestProductsItems;
    }

    public interface OnItemClickListener{
        void onItemClick(String label,String id);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public BestProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.best_products_list_item,parent,false);
        return new BestProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BestProductsViewHolder holder, int position) {
        BestProductsItem item = bestProductsItems.get(position);
        //holder.image.setImageResource(item.getBestProductsImage());
        holder.title.setText(item.getBestProductsTitle());
        holder.price.setText(item.getBestProductsOriginalPrice());
        holder.crossedPrice.setText(item.getBestProductsCrossedPrice());
        holder.crossedPrice.setPaintFlags(holder.crossedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(context)
                .load(item.getBestProductsImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return bestProductsItems.size();
    }

    public class BestProductsViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        public TextView title;
        public TextView price;
        public TextView crossedPrice;
        public ProgressBar progressBar;

        public BestProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.best_product_image);
            title = itemView.findViewById(R.id.best_product_title);
            price = itemView.findViewById(R.id.original_price);
            crossedPrice = itemView.findViewById(R.id.crossed_price);
            progressBar = itemView.findViewById(R.id.best_sales_progress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            //z is the label id for the best sales
                            //we named it like that so that it wouldn't bel loaded in the deals recyclerview
                            listener.onItemClick("z",bestProductsItems.get(position).getBestProductsID());
                        }
                    }
                }
            });
        }
    }
}
