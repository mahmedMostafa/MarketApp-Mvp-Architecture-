package com.example.market.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.market.details.ui.DetailsActivity;
import com.example.market.pojos.DealsItem;
import com.example.market.pojos.GridItem;
import com.example.market.pojos.LabelItem;
import com.example.market.pojos.PicturesItem;
import com.example.market.R;

import java.util.ArrayList;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class DealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TextView offer;
    private ArrayList<Object> listItems;
    private Context context;
    private RecyclerView.RecycledViewPool recycledViewPool;
    public GridRecyclerAdapter gridRecyclerAdapter;
    private static final int LABEL  = 0, GRID = 1 , PICTURES = 2;
    private OnLabelClickListener listener;

    public interface OnLabelClickListener {
        void OnItemClick(int position);
    }

    public void setOnLabelClickListener(OnLabelClickListener listener){
        this.listener = listener;
    }

    public DealsAdapter(ArrayList<Object> dealsList, Context context) {
        this.listItems = dealsList;
        this.context = context;
        this.recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        if(listItems.get(position) instanceof LabelItem){
            return LABEL;
        }else if (listItems.get(position) instanceof DealsItem){
            return GRID;
        }else if (listItems.get(position) instanceof PicturesItem){
            return PICTURES;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case GRID :
                View view = inflater.inflate(R.layout.deals_list_item,parent,false);
                holder = new DealsViewHolder(view);
                break;
            case LABEL :
                View view2 = inflater.inflate(R.layout.label_item,parent,false);
                holder = new LabelViewHolder(view2);
                break;

            case PICTURES :
                View view3 = inflater.inflate(R.layout.pictures_offers_item,parent,false);
                holder = new PicturesViewHolder(view3);
                break;
            default :
                View view4 = inflater.inflate(R.layout.label_item,parent,false);
                holder = new LabelViewHolder(view4);
                break;
        }
        return holder;
    }

    public void updateGridList(ArrayList<GridItem> gridItems){
        gridRecyclerAdapter.notifyDataSetChanged();
        gridRecyclerAdapter.updateList(gridItems);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case GRID :
                 DealsItem dealsItem = (DealsItem) listItems.get(position);
                final DealsViewHolder dealsViewHolder = (DealsViewHolder) holder;
                final ArrayList<GridItem> singleGridItems = dealsItem.getItemsList();
                gridRecyclerAdapter = new GridRecyclerAdapter(singleGridItems,context);
                dealsViewHolder.recyclerView.setHasFixedSize(true);
                dealsViewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context,2));
                dealsViewHolder.recyclerView.setAdapter(gridRecyclerAdapter);
                dealsViewHolder.recyclerView.setRecycledViewPool(recycledViewPool);
                gridRecyclerAdapter.setOnItemClickListener(new GridRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(context, DetailsActivity.class);
                        DealsItem dealsItem1 = (DealsItem) listItems.get(dealsViewHolder.getAdapterPosition());
                        ArrayList<GridItem> gridItems = dealsItem1.getItemsList();
                        LabelItem labelItem = (LabelItem) listItems.get(dealsViewHolder.getAdapterPosition()-1);
                        intent.putExtra(KEY_LABEL_ID,labelItem.getId());
                        intent.putExtra(KEY_GRID_ID,gridItems.get(position).getId());
                        context.startActivity(intent);
                    }
                });
                break;

            case LABEL :
                LabelItem labelItem = (LabelItem) listItems.get(position);
                LabelViewHolder labelViewHolder = (LabelViewHolder) holder;
                labelViewHolder.cardView.setBackgroundResource(R.drawable.label_background);
                labelViewHolder.titleTextView.setText(labelItem.getTitle());
                if(!(labelItem.getDescription().equals("none"))){
                    labelViewHolder.descriptionTextView.setVisibility(View.VISIBLE);
                    labelViewHolder.descriptionTextView.setText(labelItem.getDescription());
                }
                offer = labelViewHolder.descriptionTextView;
                labelViewHolder.seeAllTextView.setText("See All");
                break;

            case PICTURES :
                final PicturesItem picturesItem = (PicturesItem) listItems.get(position);
                final PicturesViewHolder picturesViewHolder = (PicturesViewHolder) holder;
                Glide.with(context)
                        .load(picturesItem.getFirstPicture())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                if(!picturesItem.getFirstPicture().equals("")){
                                    picturesViewHolder.firstProgress.setVisibility(View.GONE);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if(!picturesItem.getSecondPicture().equals("")){
                                    picturesViewHolder.secondProgress.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(picturesViewHolder.firstImage);

                Glide.with(context)
                        .load(picturesItem.getSecondPicture())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                if(!picturesItem.getSecondPicture().equals("")){
                                    picturesViewHolder.secondProgress.setVisibility(View.GONE);
                                }
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if(!picturesItem.getSecondPicture().equals("")){
                                    picturesViewHolder.secondProgress.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(picturesViewHolder.secondImage);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class DealsViewHolder extends RecyclerView.ViewHolder{

        public RecyclerView recyclerView;

        public DealsViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.deals_recycler_view);
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
            });/*
            gridRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //context.startActivity(new Intent(context,DetailsActivity.class));
                    Toast.makeText(context, "clicked :" + position, Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView seeAllTextView;
        public CardView cardView;
        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.labels_card_view);
            titleTextView = itemView.findViewById(R.id.label_title);
            descriptionTextView = itemView.findViewById(R.id.description);
            seeAllTextView = itemView.findViewById(R.id.see_all);
            seeAllTextView.setOnClickListener(new View.OnClickListener() {
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

    public class PicturesViewHolder extends RecyclerView.ViewHolder{

        public ImageView firstImage;
        public ImageView secondImage;
        public ProgressBar firstProgress;
        public ProgressBar secondProgress;

        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            firstImage = itemView.findViewById(R.id.firs_picture);
            secondImage = itemView.findViewById(R.id.second_picture);
            firstProgress = itemView.findViewById(R.id.first_offer_progress);
            secondProgress = itemView.findViewById(R.id.second_offer_progress);
        }
    }
}
