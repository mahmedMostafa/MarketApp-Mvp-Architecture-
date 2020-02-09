package com.example.market.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.market.allDeals.AllDealsActivity;
import com.example.market.details.ui.DetailsActivity;
import com.example.market.home.adapters.BestProductsAdapter;
import com.example.market.home.adapters.DealsAdapter;
import com.example.market.home.adapters.SlidingAdapter;
import com.example.market.home.adapters.TopCategoriesAdapter;
import com.example.market.pojos.DealsItem;
import com.example.market.pojos.GridItem;
import com.example.market.pojos.LabelItem;
import com.example.market.pojos.PicturesItem;
import com.example.market.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class HomeFragment extends Fragment implements HomeView {

    //Best Products RecyclerView
    private RecyclerView bestProductsRecyclerView;
    private BestProductsAdapter bestProductsAdapter;

    //Deals RecyclerView
    private RecyclerView dealsRecyclerView;
    private DealsAdapter dealsAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Object> dealsItems;

    //Top Categories RecyclerView
    private RecyclerView categoriesRecyclerView;
    private TopCategoriesAdapter categoriesAdapter;

    //Image Slider
    private ViewPager viewPager;
    private static int NUM_PAGES = 0;
    private int currentPosition = 1;
    private int lastPosition = 6;
    private Handler autoScrollHandler;
    private SlidingAdapter slidingAdapter;

    //Dots Indicators
    private LinearLayout sliderDotSpanel;
    private static final int dotsCount = 5;
    private ImageView[] dots;

    //Connection of logic
    private HomePresenter homePresenter;

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Don't forget to write the code here before the return statement
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homePresenter = new HomePresenter(getActivity(), this);
        initEveryThing(view);
        return view;
    }

    private void initEveryThing(View view){
        setImageSlider(view);
        initAutoSlider();
        settingUpTopCategoriesRecyclerView(view);
        settingUpDealsRecyclerView(view);
        initializeBestProducts(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this method doesn't call onDestroy and keeps the activity context alive so that there won't be any null pointer exceptions for rotations
        //this is not the best practice as this may cause memory leaks but it works just fine at least for now
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
            viewPager.setCurrentItem(viewPager.getCurrentItem());
            initAutoSlider();
        }
    }

    //this method is to set up the top categories recycler view and the gridRecyclerAdapter together
    private void settingUpTopCategoriesRecyclerView(View view) {

        gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        //gridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        categoriesRecyclerView = view.findViewById(R.id.top_categories_recycler_view);
        categoriesAdapter = new TopCategoriesAdapter(getActivity(), homePresenter.getTopCategoriesList());
        categoriesRecyclerView.setHasFixedSize(true);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        categoriesAdapter.setOnItemClickListener(new TopCategoriesAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), AllDealsActivity.class);
                intent.putExtra(KEY_LABEL_ID, homePresenter.getTopCategoriesList().get(position).getCategory());
                startActivity(intent);
            }
        });
    }

    private void settingUpDealsRecyclerView(View view) {
        dealsItems = new ArrayList<>();
        createDummyDataForTheDealsRecyclerView();
        dealsRecyclerView = view.findViewById(R.id.deals_items_recycler_view);
        dealsRecyclerView.setHasFixedSize(true);
        dealsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dealsAdapter = new DealsAdapter(dealsItems, getActivity());
        dealsRecyclerView.setAdapter(dealsAdapter);
        dealsAdapter.setOnLabelClickListener(new DealsAdapter.OnLabelClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), AllDealsActivity.class);
                LabelItem labelItem = (LabelItem) dealsItems.get(position);
                intent.putExtra(KEY_LABEL_ID, labelItem.getId());
                startActivity(intent);
            }
        });
    }

    private void createDummyDataForTheDealsRecyclerView() {
        for (int j = 1; j <= 6; j++) {

            LabelItem labelItem = homePresenter.getLabelList().get(j - 1);
            dealsItems.add(labelItem);

            DealsItem item = homePresenter.getDealsList().get(j - 1);
            dealsItems.add(item);

            PicturesItem picturesItem = homePresenter.getPicturesList().get(j - 1);
            dealsItems.add(picturesItem);

        }
    }


    private void initializeBestProducts(View view) {
        bestProductsRecyclerView = view.findViewById(R.id.best_products_recycler_view);
        bestProductsRecyclerView.setHasFixedSize(true);
        bestProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        bestProductsAdapter = new BestProductsAdapter(getActivity(), homePresenter.getBestProductsList());
        bestProductsRecyclerView.setAdapter(bestProductsAdapter);
        bestProductsAdapter.setOnItemClickListener(new BestProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String label, String id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(KEY_LABEL_ID, label);
                intent.putExtra(KEY_GRID_ID, id);
                startActivity(intent);
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setImageSlider(View view) {
        //instead of for loop
        //Collections.addAll(adsList, IMAGES);
        ;
        viewPager = view.findViewById(R.id.view_pager);
        sliderDotSpanel = view.findViewById(R.id.slider_dots);
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotSpanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));

        slidingAdapter = new SlidingAdapter(getActivity(), homePresenter.getAdsList());
        viewPager.setAdapter(slidingAdapter);
        viewPager.setCurrentItem(1);
        if (isAdded()) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;

                    for (int i = 0; i < dotsCount; i++) {
                        //dots[i] = new ImageView(getActivity());
//                        dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
                        dots[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.non_active_dot));
                    }

                    if (position == 0) {
                        dots[4].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));
                    } else if (position == 6) {
                        dots[0].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));
                    } else {
                        dots[position - 1].setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.active_dot));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (currentPosition == 0)
                        viewPager.setCurrentItem(lastPosition - 1, false);
                    if (currentPosition == lastPosition)
                        viewPager.setCurrentItem(1, false);
                }
            });
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        initAutoSlider();
                    } else {
                        if (autoScrollHandler != null) {
                            autoScrollHandler.removeCallbacksAndMessages(null);
                            autoScrollHandler = null;
                        }
                    }
                    //init the slider here so that after stopping the swipe the auto sliding begins again
                    initAutoSlider();
                    return false;
                }
            });
        }
    }


    private void initAutoSlider() {
        NUM_PAGES = 7;

        // Auto start of viewpager
        if (autoScrollHandler == null)
            autoScrollHandler = new Handler();
        autoScrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = viewPager.getCurrentItem();
                position++;
                if (position == NUM_PAGES) position = 0;
                //slidingAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(position, true);
                autoScrollHandler.postDelayed(this, 4000);
            }
        }, 4000);
    }

    @Override
    public void notifyDealsAdapter() {
        dealsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateDealsAdapter(int k, ArrayList<GridItem> items) {
        dealsAdapter.updateGridList(items);
        DealsItem dealsItem = new DealsItem();
        dealsItem.setItemsList(items);
        homePresenter.getDealsList().set(k, dealsItem);
        dealsAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyTopCategoriesAdapter() {
        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void sliderError() {
        Toast.makeText(getActivity(), "Couldn't load slider", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bestProductsError() {
        Toast.makeText(getActivity(), "Couldn't load best products", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyBestProductsAdapter() {
        bestProductsAdapter.notifyDataSetChanged();
    }

}
