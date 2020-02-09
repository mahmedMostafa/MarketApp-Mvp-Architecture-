package com.example.market.favourites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.details.ui.DetailsActivity;
import com.example.market.R;
import com.example.market.auth.login.LoginActivity;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class FavoritesFragment extends Fragment implements FavouritesView, View.OnClickListener {


    private LinearLayout loginLayout;
    private Button loginButton;
    private RecyclerView recyclerView;
    private FavouritesAdapter adapter;
    private LinearLayout noLikesLayout;
    private ProgressBar progressBar;

    private FavouritesPresenter favouritesPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initView(view);
        favouritesPresenter = new FavouritesPresenter(getActivity(), this);
        initRecyclerView(view);
        return view;
    }

    private void initView(View view) {
        loginLayout = view.findViewById(R.id.login_layout);
        loginButton = view.findViewById(R.id.favourites_login_button);
        noLikesLayout = view.findViewById(R.id.no_likes_layout);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favourites_login_button:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        favouritesPresenter.onStart();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        favouritesPresenter.onStop();
    }


    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.favourites_recycler_view);
        progressBar = view.findViewById(R.id.favourites_progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new FavouritesAdapter(getActivity(), favouritesPresenter.getFavouriteItems());
        recyclerView.setAdapter(adapter);
        adapter.setOnCloseButtonClickListener(new FavouritesAdapter.onCloseButtonClickListener() {
            @Override
            public void onItemClick(int position) {
                adapter.notifyItemRemoved(position);
                favouritesPresenter.removeItem(position);
                favouritesPresenter.getFavouriteItems().remove(favouritesPresenter.getFavouriteItems().get(position));
                if (favouritesPresenter.getFavouriteItems().size() == 0) {
                    noLikesLayout.setVisibility(View.VISIBLE);
                } else {
                    noLikesLayout.setVisibility(View.GONE);
                }
            }
        });

        adapter.setOnItemClickListener(new FavouritesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(KEY_LABEL_ID, favouritesPresenter.getFavouriteItems().get(position).getCategory());
                intent.putExtra(KEY_GRID_ID, favouritesPresenter.getFavouriteItems().get(position).getItem());
                startActivity(intent);
            }
        });

        Log.e("size favouriteItems :", String.valueOf(favouritesPresenter.getFavouriteItems().size()));
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateFavouritesAdapter() {
        adapter.setFavouriteItems(favouritesPresenter.getFavouriteItems());
    }

    @Override
    public void hideNoLikesLayout() {
        noLikesLayout.setVisibility(View.GONE);
    }

    @Override
    public void failedLoadingFavourites() {
        Toast.makeText(getActivity(), "Failed Loading Favourites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemDeleted() {
        Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoLikesLayout() {
        noLikesLayout.setVisibility(View.VISIBLE);
        noLikesLayout.setClickable(false);
    }

    @Override
    public void showLoginLayout() {
        loginLayout.setVisibility(View.VISIBLE);
    }


}
