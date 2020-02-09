package com.example.market.details.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.market.auth.login.LoginActivity;
import com.example.market.details.ui.DetailsView;
import com.example.market.pojos.GridItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DetailsPresenter {

    private DetailsView view;
    private Context context;
    private FirebaseFirestore fireStore;
    private StorageReference storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private String label_id;
    private boolean isLiked = false;

    private GridItem currentGridItem = new GridItem();;

    public DetailsPresenter(Context context,DetailsView view) {
        this.view = view;
        this.context = context;
        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    void setCurrentGridItem(GridItem gridItem){
        currentGridItem = gridItem;
    }

    private void addToCart() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    view.showToast("You must login first");
                } else {
                    DocumentReference document = fireStore.collection("users/").document(firebaseAuth.getCurrentUser().getUid())
                            .collection("cart").document(currentGridItem.getId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("imageUrl", currentGridItem.getImageUrl());
                    map.put("title", currentGridItem.getTitle());
                    map.put("price", currentGridItem.getPrice());
                    map.put("oldPrice", currentGridItem.getOldPrice());
                    map.put("freeShipping", currentGridItem.isFreeShipping());
                    map.put("category", label_id);
                    map.put("id", currentGridItem.getId());
                    map.put("brand", currentGridItem.getBrand());
                    map.put("discount", currentGridItem.getDiscount());
                    document.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.showToast("Cart Added");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            view.showToast("Cart not Added!");
                            Log.e("error is :", e.getMessage());
                        }
                    });
                }
            }
        };
        firebaseAuth.addAuthStateListener(listener);
    }

    private void likeOrDislike() {
        if(firebaseAuth.getCurrentUser() != null){

            DocumentReference document = fireStore.collection("users/").document(firebaseAuth.getCurrentUser().getUid())
                    .collection("likes/").document(currentGridItem.getId());
            if (!isLiked) {
                Map<String, Object> map = new HashMap<>();
                map.put("category", label_id);
                map.put("imageUrl", currentGridItem.getImageUrl());
                map.put("price", currentGridItem.getPrice());
                map.put("oldPrice", currentGridItem.getOldPrice());
                map.put("title", currentGridItem.getTitle());
                map.put("discount", currentGridItem.getDiscount());
                map.put("brand", currentGridItem.getBrand());
                document.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showToast("Item added to your favourites!");
                    }
                });
                //Glide.with(context).load(R.drawable.ic_liked_favourite).into(favouriteButton);
                view.setLikeButton();
                isLiked = true;
            } else {
                document.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showToast("Item deleted from your favourites");
                    }
                });
               // Glide.with(DetailsActivity.this).load(R.drawable.ic_unliked_favourite).into(favouriteButton);
                view.setUnlikedButton();
                isLiked = false;
            }
        }else{
            context.startActivity(new Intent(context, LoginActivity.class));
            view.showToast("you must login first!!");
        }
    }


}
