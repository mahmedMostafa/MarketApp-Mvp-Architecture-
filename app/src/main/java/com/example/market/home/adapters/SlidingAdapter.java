package com.example.market.home.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.market.R;

import java.util.ArrayList;

public class SlidingAdapter extends PagerAdapter {

    private ArrayList<String> adsList;
    private Context context;
    private LayoutInflater inflater;

    public SlidingAdapter(Context context, ArrayList<String> ads){

        this.adsList = ads;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        View view = inflater.inflate(R.layout.sliding_ads_layout,container,false);
        assert view !=null;

        final ImageView imageView = view.findViewById(R.id.image);
        final ProgressBar progressBar = view.findViewById(R.id.progress);
        Glide.with(context)
                .load(adsList.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if(!adsList.get(position).equals("") || adsList.get(position).length() <= 0){
                            progressBar.setVisibility(View.GONE);
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(!adsList.get(position).equals("") || adsList.get(position).length() <= 0){
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(imageView);
        container.addView(view,0);
        return view;
    }

    @Override
    public int getCount() {
        return adsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
