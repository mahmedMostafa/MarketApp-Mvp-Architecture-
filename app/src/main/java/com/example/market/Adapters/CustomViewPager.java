package com.example.market.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private boolean enableSwipe ;

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        enableSwipe = true;
    }

    //we can just return false if we want it to be disabled all time
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //never allow swiping to switch between pages
        return enableSwipe && super.onInterceptTouchEvent(ev);
    }

    //we can just return false if we want it to be disabled all time
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //never allow swiping to switch between pages
        return enableSwipe && super.onTouchEvent(ev);
    }

    //this method that would be called to determine swiping or not
    public void setEnableSwipe(boolean enableSwipe){
        this.enableSwipe = enableSwipe;
    }
}
