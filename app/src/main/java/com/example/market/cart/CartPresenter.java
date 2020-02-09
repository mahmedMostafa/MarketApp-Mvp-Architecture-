package com.example.market.cart;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.market.pojos.CartItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartPresenter {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<CartItem> cartItems = new ArrayList<>();
    private int totalPrice =0;
    private String currentPrice;

    private Context context;
    private CartView cartView;

    public CartPresenter(Context context, CartView cartView) {
        this.context = context;
        this.cartView = cartView;
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        checkUser();
        getItems();
    }

    void onStart(){
        mAuth.addAuthStateListener(listener);
    }
    void onStop(){
        if (mAuth != null) {
            mAuth.addAuthStateListener(listener);
        }
    }
    private void checkUser() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    cartView.hideProgress();
                    cartView.showLoginLayout();
                }
            }
        };
    }

    public List<CartItem> getCartItems(){
        return cartItems;
    }

    private void getItems() {
        if (mAuth.getCurrentUser() != null) {
            //setting total price to 0 as i don't knwo whay it gets a high value from no where :D
            totalPrice =0;
            Log.e("total price :" , String.valueOf(totalPrice));
            //noItemsLayout.setVisibility(View.GONE);
            firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                    .collection("cart/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        cartView.hideProgress();
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documentSnapshots) {
                            /*CartItem cartItem = document.toObject(CartItem.class);
                            cartItems.add(cartItem);*/
                            CartItem cartItem = new CartItem();
                            cartItem.setId(document.getId());
                            cartItem.setCategory(document.get("category").toString());
                            cartItem.setFreeShipping(Boolean.parseBoolean(document.get("freeShipping").toString()));
                            cartItem.setImageUrl(document.get("imageUrl").toString());
                            cartItem.setPrice(document.get("price").toString());
                            cartItem.setOldPrice(document.get("oldPrice").toString());
                            cartItem.setTitle(document.get("title").toString());
                            cartItem.setBrand(document.get("brand").toString());
                            cartItem.setDiscount(document.get("discount").toString());
//                            currentItemId = document.get("id").toString();
                            currentPrice = document.get("price").toString();
                            Log.e("current price :" , String.valueOf(getPrice(currentPrice)));
                            totalPrice+=getPrice(currentPrice);
                            Log.e("total price :" , String.valueOf(totalPrice));
                            cartItems.add(cartItem);
                        }
                        cartView.updateCartAdapter();
                        if (cartItems.size() == 0) {
                            cartView.showNoItemsLayout();
                            cartView.hideCompleteOrderLayout();
                        } else {
                            cartView.showCompleteOrderLayout();
                            cartView.hideNoItemLayout();
                        }
                        cartView.setTotalPrice();
                    }
                }
            });
            Log.e("cartItems size is :", String.valueOf(cartItems.size()));
            /*if(cartItems.size() ==0){
                noItemsLayout.setVisibility(View.VISIBLE);
            }*/
        }
    }

    int getTotalPrice(){
        return totalPrice;
    }

    void removeItem(int position){
        firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                .collection("cart/").document(cartItems.get(position).getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cartView.itemRemovedToast();
                    }
                });
    }

    //this method add the coma to the price to make it look better
    String addComa(String price){
        if(price.length() >=4){
            int length = price.length();
            int position = ((length+1)/2)-1;
            return price.substring(0,position) + ',' + price.substring(position);
        }else{
            return price;
        }
    }

    //as we stored the price as a string so we convert it into integer with splitting the EGP and , of course
    private int getPrice(String price){
        String [] parts = price.split(" ");
        String priceString = parts[1];
        String finalPrice ="";
        for(int i=0;i<priceString.length();i++){
            if(priceString.charAt(i) == ','){
                continue;
            }else{
                finalPrice+=priceString.charAt(i);
            }
        }
        return Integer.parseInt(finalPrice);
    }
}
