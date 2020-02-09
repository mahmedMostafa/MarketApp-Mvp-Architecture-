package com.example.market.recentlyViewed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.details.ui.DetailsActivity;
import com.example.market.Adapters.ViewedItemsAdapter;
import com.example.market.database.ViewedItem;
import com.example.market.R;

import java.util.Collections;
import java.util.List;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class RecentlyViewedActivity extends AppCompatActivity {

    public static final String KEY_VIEWED_ITEM ="viewed_item";

    private ViewedItemViewModel viewModel;
    private TextView noViewedItems;
    private RelativeLayout clearViewedItemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_recently_viewed);

        clearViewedItemsLayout = findViewById(R.id.clear_recently_viewed_layout);
        noViewedItems = findViewById(R.id.no_viewed_items_tv);
        RecyclerView recyclerView = findViewById(R.id.recently_viewed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ViewedItemsAdapter adapter = new ViewedItemsAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ViewedItemViewModel.class);
        viewModel.getAllItems().observe(this, new Observer<List<ViewedItem>>() {
            @Override
            public void onChanged(List<ViewedItem> viewedItems) {
                if(viewedItems.size() > 0){
                    noViewedItems.setVisibility(View.GONE);
                }
                for(int i=0;i<viewedItems.size();i++){
                    for(int j=i+1;j<viewedItems.size();j++){
                        if(viewedItems.get(i).getItem().equals(viewedItems.get(j).getItem())){
                            //it is a must to delete the i one so that the item shows up in the top
                            viewModel.delete(viewedItems.get(i));
                        }
                    }
                }
                adapter.setViewedItemsList(viewedItems);
                //so it will show the last item viewed at the top
                Collections.reverse(viewedItems);

            }
        });

        clearViewedItemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteAllItems();
                noViewedItems.setVisibility(View.VISIBLE);
            }
        });


        adapter.setOnItemClickListener(new ViewedItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String label, String id) {
                Intent intent = new Intent(RecentlyViewedActivity.this, DetailsActivity.class);
                intent.putExtra(KEY_LABEL_ID,label);
                intent.putExtra(KEY_GRID_ID,id);
                startActivity(intent);
            }
        });
    }
}
