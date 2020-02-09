package com.example.market.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.details.ui.DetailsActivity;
import com.example.market.main.MainActivity;
import com.example.market.R;
import com.example.market.auth.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;


public class CartFragment extends Fragment implements CartView {

    private LinearLayout loginLayout;
    private LinearLayout noItemsLayout;
    private Button loginButton;
    private Button continueShoppingButton;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private RelativeLayout completeOrderLayout;
    private TextView totalPriceTextView;
    private ProgressBar progressBar;

    private CartPresenter cartPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews(view);
        cartPresenter = new CartPresenter(getActivity(), this);
        initRecyclerView();
        return view;
    }

    private void initViews(View view) {
        loginLayout = view.findViewById(R.id.login_layout);
        progressBar = view.findViewById(R.id.cart_progress_bar);
        noItemsLayout = view.findViewById(R.id.no_items_layout);
        totalPriceTextView = view.findViewById(R.id.total_price_text_view);
        completeOrderLayout = view.findViewById(R.id.complere_order_layout);
        continueShoppingButton = view.findViewById(R.id.continue_shopping_button);
        final TabLayout tabLayout = (TabLayout) ((MainActivity) getActivity()).findViewById(R.id.sliding_tabs);
        loginButton = view.findViewById(R.id.cart_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click removeListener :", "i'm here");
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        continueShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just switch to the first home tab
                tabLayout.getTabAt(0).select();
            }
        });
    }

    private void initRecyclerView() {
        adapter = new CartAdapter(cartPresenter.getCartItems(), getContext());
        recyclerView.setAdapter(adapter);
        adapter.setOnRemoveItemClickListener(new CartAdapter.OnRemoveItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                adapter.notifyItemRemoved(position);
                cartPresenter.removeItem(position);
                cartPresenter.getCartItems().remove(cartPresenter.getCartItems().get(position));
                reloadFragment();
                if (cartPresenter.getCartItems().size() == 0) {
                    noItemsLayout.setVisibility(View.VISIBLE);
                    completeOrderLayout.setVisibility(View.GONE);
                } else {
                    noItemsLayout.setVisibility(View.GONE);
                    completeOrderLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(KEY_LABEL_ID, cartPresenter.getCartItems().get(position).getCategory());
                intent.putExtra(KEY_GRID_ID, cartPresenter.getCartItems().get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        cartPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        cartPresenter.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        reloadFragment();
    }

    private void reloadFragment() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideLoginLayout() {
        loginLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoItemsLayout() {
        noItemsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoItemLayout() {
        noItemsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showCompleteOrderLayout() {
        completeOrderLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCompleteOrderLayout() {
        completeOrderLayout.setVisibility(View.GONE);
    }

    @Override
    public void setTotalPrice() {
        totalPriceTextView.setText("EGP " + cartPresenter.addComa(String.valueOf(cartPresenter.getTotalPrice())));
    }

    @Override
    public void itemRemovedToast() {
        Toast.makeText(getActivity(), "Item Removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCartAdapter() {
        adapter.setCartItems(cartPresenter.getCartItems());
    }

    @Override
    public void showLoginLayout() {
        loginLayout.setVisibility(View.VISIBLE);
    }
}
