package com.example.market.home.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.market.pojos.GridItem;
import com.example.market.R;

import java.util.ArrayList;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.GridViewHolder> {

    private OnItemClickListener listener;
    private ArrayList<GridItem> gridList;
    private Context context;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ArrayList<GridItem> getGridList() {
        return gridList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public GridRecyclerAdapter(ArrayList<GridItem> gridList, Context context) {
        this.gridList = gridList;
        this.context = context;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_list_item, parent, false);
        final GridViewHolder holder = new GridViewHolder(view);
        return holder;
    }

    public void updateList(ArrayList<GridItem> gridItems) {
        this.gridList = gridItems;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final GridViewHolder holder, final int position) {
        final GridItem currentItem = gridList.get(position);
        String url = currentItem.getImageUrl();
        String title = currentItem.getTitle();
        String price = currentItem.getPrice();
        String discount = currentItem.getDiscount();
        Log.e("discount", gridList.get(0).getDiscount() + "");
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (!currentItem.getImageUrl().equals("")) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (!currentItem.getImageUrl().equals("")) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.titleTextView.setText(title);
        holder.priceTextView.setText(price);

        if(!(currentItem.getDiscount().equals(""))){
            holder.discountTextView.setText(currentItem.getDiscount());
            holder.discountTextView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //Pair<View,String> pair1 = Pair.create((View) holder.imageView,holder.imageView.getTransitionName());
                    //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.imageView, ViewCompat.getTransitionName(holder.imageView) );
                    if (listener != null) {
                        int position = holder.getAdapterPosition();
                        listener.onItemClick(position);
                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gridList.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView priceTextView;
        public TextView discountTextView;
        public ProgressBar progressBar;

        public GridViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image_view);
            titleTextView = itemView.findViewById(R.id.grid_title_text_view);
            priceTextView = itemView.findViewById(R.id.grid_price_text_view);
            progressBar = itemView.findViewById(R.id.grid_progress);
            discountTextView = itemView.findViewById(R.id.discount_text_view);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (removeListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            GridItem gridItem = gridList.get(position);
                            removeListener.onItemClick(new Gson().toJson(gridItem));
                        }
                    }
                }
            });*/
        }
    }
}
