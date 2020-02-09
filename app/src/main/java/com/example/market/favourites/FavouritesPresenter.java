package com.example.market.favourites;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.market.pojos.FavouriteItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FavouritesPresenter {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener listener;
    private FirebaseFirestore firebaseFirestore;
    private List<FavouriteItem> favouriteItems = new ArrayList<>();

    private Context context;
    private FavouritesView favouritesView;

    FavouritesPresenter(Context context, FavouritesView favouritesView) {
        this.context = context;
        this.favouritesView = favouritesView;
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        checkUser();
        getAllFavourites();
    }

    void onStart(){
        mAuth.addAuthStateListener(listener);
    }

    void onStop(){
        if (mAuth != null) {
            mAuth.removeAuthStateListener(listener);
        }
    }

    void removeItem(int position){
        firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                .collection("likes/").document(favouriteItems.get(position).getItem())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                favouritesView.itemDeleted();
            }
        });
    }

    private void checkUser() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    favouritesView.hideNoLikesLayout();
                    favouritesView.hideProgress();
                    favouritesView.showLoginLayout();
                }
            }
        };
    }

    List<FavouriteItem> getFavouriteItems(){
        return favouriteItems;
    }

     private void getAllFavourites() {
        favouriteItems.clear();
        if (mAuth.getCurrentUser() != null) {
            favouritesView.hideNoLikesLayout();
            firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                    .collection("likes/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        favouritesView.hideProgress();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            FavouriteItem item = new FavouriteItem();

                            item.setItem(document.getId());
                            item.setBrand(document.get("brand").toString());
                            item.setCategory(document.get("category").toString());
                            item.setImageUrl(document.get("imageUrl").toString());
                            item.setPrice(document.get("price").toString());
                            item.setOldPrice(document.get("oldPrice").toString());
                            item.setTitle(document.get("title").toString());
                            item.setDiscount(document.get("discount").toString());

                            favouriteItems.add(item);
                        }
                        favouritesView.updateFavouritesAdapter();
                        Collections.reverse(favouriteItems);
                        if (favouriteItems.size() == 0) {
                            favouritesView.showNoLikesLayout();
                        } else {
                            favouritesView.hideNoLikesLayout();
                        }
                    }
                }
            });
        }else{
            favouritesView.failedLoadingFavourites();
        }
    }

}
