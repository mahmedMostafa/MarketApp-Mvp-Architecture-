package com.example.market.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.market.pojos.CartItem;
import com.example.market.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private Context context;

    private OnRemoveItemClickListener removeListener;
    private OnItemClickListener itemListener;

    public interface OnRemoveItemClickListener {
        void OnItemClick(int position);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnRemoveItemClickListener(OnRemoveItemClickListener listener) {
        this.removeListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemListener =listener;
    }

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    public void setCartItems(List<CartItem> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem currentItem = cartItems.get(position);
        Glide.with(context)
                .load(currentItem.getImageUrl()).into(holder.imageView);
        holder.title.setText(currentItem.getTitle());
        holder.price.setText(currentItem.getPrice());
        holder.oldPrice.setText(currentItem.getOldPrice());
        if (currentItem.isFreeShipping()) {
            holder.freeShipping.setVisibility(View.VISIBLE);
        } else {
            holder.freeShipping.setVisibility(View.GONE);
        }
        //holder.freeShipping.setText("Free Shipping");
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView title;
        public TextView price;
        public TextView oldPrice;
        public TextView freeShipping;
        public TextView removeItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cart_image);
            title = itemView.findViewById(R.id.cart_title);
            price = itemView.findViewById(R.id.cart_price);
            oldPrice = itemView.findViewById(R.id.cart_old_price);
            freeShipping = itemView.findViewById(R.id.cart_free_shipping);
            removeItemButton = itemView.findViewById(R.id.remove_item_cart);

            removeItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (removeListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            removeListener.OnItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            itemListener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
