package com.example.market.home.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.pojos.AllDealsItem;
import com.example.market.R;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class AllDealsAdapter extends RecyclerView.Adapter<AllDealsAdapter.AllDealsViewHolder> {

    private List<AllDealsItem> allDealsItems;
    private Context context;
    private OnItemClickListener listener;

    public AllDealsAdapter(List<AllDealsItem> allDealsItems, Context context) {
        this.allDealsItems = allDealsItems;
        this.context = context;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllDealsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_deals_item,parent,false);
        return new AllDealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllDealsViewHolder holder, int position) {
        AllDealsItem currentItem = allDealsItems.get(position);
        holder.dealsPrice.setText(currentItem.getDealsPrice());
        holder.dealsBrand.setText(currentItem.getDealsBrand());
        if(currentItem.getDealsOldPrice().equals("none")){
            holder.dealsOldPrice.setVisibility(View.GONE);
        }else{
            holder.dealsOldPrice.setText(currentItem.getDealsOldPrice());
            holder.dealsOldPrice.setPaintFlags(holder.dealsOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if(currentItem.getDealsDiscount().equals("")){
            holder.dealsDiscount.setVisibility(View.GONE);
        }else{
            holder.dealsDiscount.setText(currentItem.getDealsDiscount());
        }
        Glide.with(context).load(currentItem.getDealsImageUrl()).into(holder.dealsImage);
        if(!(currentItem.isDealsFreeShipping())){
            holder.relativeLayout.setVisibility(View.GONE);
        }
        holder.dealsRatingBar.setRating(Float.parseFloat(String.valueOf(currentItem.getDealsRating())));
        holder.dealsTitle.setText(currentItem.getDealsTitle());

    }

    @Override
    public int getItemCount() {
        return allDealsItems.size();
    }

    public class AllDealsViewHolder extends RecyclerView.ViewHolder{

        public TextView dealsBrand;
        public TextView dealsPrice;
        public ImageView dealsImage;
        public TextView dealsOldPrice;
        public MaterialRatingBar dealsRatingBar;
        public TextView dealsDiscount;
        public RelativeLayout relativeLayout;
        public TextView dealsTitle;

        public AllDealsViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.deals_free_shipping_layout);
            dealsBrand = itemView.findViewById(R.id.deals_brand);
            dealsDiscount = itemView.findViewById(R.id.deals_discount);
            dealsImage = itemView.findViewById(R.id.deals_image);
            dealsOldPrice = itemView.findViewById(R.id.deals_old_price);
            dealsRatingBar = itemView.findViewById(R.id.deals_total_bar);
            dealsPrice = itemView.findViewById(R.id.deals_price);
            dealsTitle = itemView.findViewById(R.id.deals_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
