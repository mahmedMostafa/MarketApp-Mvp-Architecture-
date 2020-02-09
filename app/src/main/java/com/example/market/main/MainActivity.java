package com.example.market.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.market.Adapters.CustomViewPager;
import com.example.market.Adapters.SimpleFragmentPagerAdapter;
import com.example.market.account.AccountActivity;
import com.example.market.cart.CartFragment;
import com.example.market.favourites.FavoritesFragment;
import com.example.market.R;
import com.example.market.home.HomeFragment;
import com.google.android.material.tabs.TabLayout;

import static com.example.market.account.AccountActivity.KEY_TAB;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_GRID_ITEM = "key_grid_item";
    public static final String KEY_LABEL_ID = "item_label_id";
    public static final String KEY_GRID_ID = "item_grid_id";

    private TabLayout tabLayout;

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //put the normal theme back after the splash theme
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this is to delete the line between the toolBar and the Tab Layout
        Toolbar toolbar = findViewById(R.id.main_tool_bar);
        //we use this if statement as setElevation might return a nullPointerException
        /*if (toolbar != null) {
            toolbar.setElevation(0);
        }*/
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(R.color.colorTitle);
        //setting up the tab layout

        setUpTabLayout();
        //this intent is from the account activity when the user press the saved items layout to navigate to the favourites fragment
        Intent intent = getIntent();
        tabLayout.getTabAt(intent.getIntExtra(KEY_TAB, 0)).select();

    }

    //this method takes care of creating the view pager , tab layout , binding them together
    // ,setting up the icons & titles for the tab layout
    private void setUpTabLayout() {
        //find the custom view pager we created which doesn't allow the user to swipe among tabs
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        viewPager.setEnableSwipe(false);
        //create an gridRecyclerAdapter that knows which fragment should show in each tab
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        //set the gridRecyclerAdapter into the view pager
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        //give the tab layout the view pager
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_drafts);

        //we first change the first icon color as the removeListener doesn't get triggered yet (i don't know why :D)
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        //--------------------------------for changing the text color--------------------------------------//
        //first parameter takes the unselected text color , the second one takes the selected text color
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorWhite), getResources().getColor(R.color.colorAccent));

        //--------------------------------for changing the icon color--------------------------------------//
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //set the colour to whatever you want
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //return to white
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "Home");
        adapter.addFrag(new FavoritesFragment(), "Favourites");
        adapter.addFrag(new CartFragment(), "Cart");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.account_item) {
            startActivity(new Intent(MainActivity.this, AccountActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

