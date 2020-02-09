package com.example.market.details.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.market.database.ViewedItem;
import com.example.market.details.presenter.DetailsPresenter;
import com.example.market.feedback.FeedbackActivity;
import com.example.market.recentlyViewed.ViewedItemViewModel;
import com.example.market.description.DescriptionActivity;
import com.example.market.pojos.GridItem;
import com.example.market.R;
import com.example.market.auth.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class DetailsActivity extends AppCompatActivity implements DetailsView{

    private ViewedItemViewModel viewModel;
    public static final String KEY_DESCRIPTION = "key_description";
    public static final String KEY_FEATURES = "key_features";
    private String descriptionText;
    private String keyFeatures;
    private TextView totalRatingTextView;
    private MaterialRatingBar materialRatingBar;
    private TextView brandTextView;
    private TextView titleTextView;
    private TextView priceTextView;
    private TextView oldPriceTextView;
    private TextView deliveryInfoTextView;
    private TextView descriptionTextView;
    private ImageView itemImageView;
    private TextView readMoreTextView;
    private TextView discountTextView;
    private TextView returnPolicyTextView;
    private TextView warrantyTextView;
    private RelativeLayout productsRatings;
    private ProgressBar progressBar;
    private LinearLayout freeShippingLayout;
    private GridItem currentGridItem;
    private FirebaseFirestore fireStore;
    private StorageReference storage;
    private Button addToCartButton;
    private ImageView favouriteButton;
    private LinearLayout ratingLayout;
    private TextView totalVotersTextView;
    private String grid_id;
    private String label_id;
    private boolean isLiked = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private Toolbar toolbar;

    private DetailsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initializeUI();
        presenter = new DetailsPresenter(this,this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        getSupportActionBar().setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(fade);
        }
        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentGridItem = new GridItem();
        storage = FirebaseStorage.getInstance().getReference();
        viewModel = ViewModelProviders.of(this).get(ViewedItemViewModel.class);

        readMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, DescriptionActivity.class);
                intent.putExtra(KEY_DESCRIPTION, currentGridItem.getDescription());
                intent.putExtra(KEY_FEATURES, currentGridItem.getKeyFeatures());
                startActivity(intent);
            }
        });
        retrieveData();
        final Gson gson = new Gson();
        productsRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, FeedbackActivity.class);
                intent.putExtra(KEY_LABEL_ID, label_id);
                intent.putExtra(KEY_GRID_ID, gson.toJson(currentGridItem));
                startActivity(intent);
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeOrDislike();
            }
        });
        checkForLikeButton();
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
                        Toast.makeText(DetailsActivity.this, "Item added to your favourites!", Toast.LENGTH_LONG).show();
                    }
                });
                Glide.with(DetailsActivity.this).load(R.drawable.ic_liked_favourite).into(favouriteButton);
                isLiked = true;
            } else {
                document.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DetailsActivity.this, "Item deleted from your favourites", Toast.LENGTH_LONG).show();
                    }
                });
                Glide.with(DetailsActivity.this).load(R.drawable.ic_unliked_favourite).into(favouriteButton);
                isLiked = false;
            }
        }else{
            startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
            Toast.makeText(this, "you must login first!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(listener);
    }

    private void addToCart() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
                    Toast.makeText(DetailsActivity.this, "You must login first", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailsActivity.this, "Cart Added!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivity.this, "Cart not Added!", Toast.LENGTH_SHORT).show();
                            Log.e("error is :", e.getMessage());
                        }
                    });
                }
            }
        };
        firebaseAuth.addAuthStateListener(listener);
    }

    private void retrieveData() {
        Intent intent = getIntent();
        grid_id = intent.getStringExtra(KEY_GRID_ID);
        label_id = intent.getStringExtra(KEY_LABEL_ID);
        //currentGridItem = new Gson().fromJson(intent.getStringExtra(KEY_GRID_ITEM),GridItem.class);
        Log.d("", "label_id is :" + label_id);
        //Log.d("","grid_id is :" + currentGridItem.getId());

        CollectionReference reference = fireStore.collection("grid_items/");
        final DocumentReference document = reference.document(label_id).collection("items/").document(grid_id);
        Log.d("", "the document is :" + document.getId());
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error is :" + e.getMessage());
                    Toast.makeText(DetailsActivity.this, "Error is :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("", "currentGridItem id is :" + grid_id);
                    GridItem gridItem = documentSnapshot.toObject(GridItem.class);

                    if (!(gridItem.getTotalRating() == 0)) {
                        ratingLayout.setVisibility(View.VISIBLE);
                        totalVotersTextView.setText("(" + gridItem.getTotalVoters() + " ratings)");
                        totalRatingTextView.setText(String.valueOf(gridItem.getTotalRating()));
                        materialRatingBar.setRating(Float.parseFloat(String.valueOf(gridItem.getTotalRating())));
                    }

                    //String title = documentSnapshot.get("title").toString();
                    currentGridItem.setTitle(gridItem.getTitle());
                    titleTextView.setText(gridItem.getTitle());

                    //String brand = documentSnapshot.get("brand").toString();
                    currentGridItem.setBrand(gridItem.getBrand());
                    brandTextView.setText(gridItem.getBrand());

                    //String price = documentSnapshot.get("price").toString();
                    currentGridItem.setPrice(gridItem.getPrice());
                    priceTextView.setText(gridItem.getPrice());

                    String oldPrice = gridItem.getOldPrice();
                    currentGridItem.setOldPrice(oldPrice);
                    if (oldPrice.equals("none")) {
                        oldPriceTextView.setVisibility(View.GONE);
                    } else {
                        oldPriceTextView.setText(oldPrice);
                        oldPriceTextView.setPaintFlags(oldPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    //String deliveryInfo = documentSnapshot.get("delivery_info").toString();
                    currentGridItem.setDeliveryInfo(gridItem.getDeliveryInfo());
                    deliveryInfoTextView.setText(gridItem.getDeliveryInfo());


                    //String returnPolicy = documentSnapshot.get("return_policy").toString();
                    currentGridItem.setReturnPolicy(gridItem.getReturnPolicy());
                    returnPolicyTextView.setText(gridItem.getReturnPolicy());

                    String warranty = gridItem.getWarranty();
                    currentGridItem.setWarranty(warranty);
                    warrantyTextView.setText(warranty);


                    String description = gridItem.getKeyFeatures();
                    String newDescription = description.replace("_b", "\n");
                    Log.d("", "the new description is :" + newDescription);
                    descriptionTextView.setText(newDescription);
                    currentGridItem.setKeyFeatures(newDescription);

                    String discount = gridItem.getDiscount();
                    currentGridItem.setDiscount(discount);
                    if (discount.equals("")) {
                        discountTextView.setVisibility(View.GONE);
                    } else {
                        discountTextView.setText(discount);
                    }
                    currentGridItem.setFreeShipping(gridItem.isFreeShipping());
                    if (!gridItem.isFreeShipping()) {
                        freeShippingLayout.setVisibility(View.GONE);
                    }

                    currentGridItem.setId(grid_id);
                    currentGridItem.setStar1(gridItem.getStar1());
                    currentGridItem.setStar2(gridItem.getStar2());
                    currentGridItem.setStar3(gridItem.getStar3());
                    currentGridItem.setStar4(gridItem.getStar4());
                    currentGridItem.setStar5(gridItem.getStar5());
                    currentGridItem.setTotalVoters(gridItem.getTotalVoters());
                    if (gridItem.getTotalRating() >= 0) {
                        currentGridItem.setTotalRating((double) gridItem.getTotalRating());
                    } else {
                        currentGridItem.setTotalRating(0);
                    }
                    currentGridItem.setDescription(gridItem.getDescription());

                    getSupportActionBar().setTitle(currentGridItem.getTitle());


                }
            }
        });
        StorageReference mRef = storage.child("grid_sales/" + grid_id + ".jpg");
        mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(DetailsActivity.this)
                        .load(uri.toString())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(itemImageView);
                currentGridItem.setImageUrl(uri.toString());
                sendViewedItem(currentGridItem.getBrand(), currentGridItem.getTitle(), currentGridItem.getPrice(), currentGridItem.getOldPrice()
                        , currentGridItem.getDiscount(), currentGridItem.getImageUrl());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("", "the image error is : " + e.getMessage());
                Toast.makeText(DetailsActivity.this, "Can't Load image!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            //onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    private void sendViewedItem(String brand, String title, String price, String oldPrice, String discount, String imageUrl) {

        ViewedItem viewedItem = new ViewedItem(label_id, grid_id, imageUrl, brand, title, price, oldPrice, discount);
        viewModel.insert(viewedItem);
        Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
        /*LiveData<List<ViewedItem>> list = viewModel.getAllItems();
        for(int i=0;i<list.getValue().size();i++){
            if(viewedItem == list.getValue().get(i)){
                Toast.makeText(this, "Already exist!!", Toast.LENGTH_SHORT).show();
            }else{
                viewModel.insert(viewedItem);
            }
        }*/
    }

    private void checkForLikeButton() {
        if(firebaseAuth.getCurrentUser() != null){
            fireStore.collection("users/").document(firebaseAuth.getCurrentUser().getUid())
                    .collection("likes/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        for (DocumentSnapshot document : docs) {
                            //if any of the favourites items equal the one being reviewed set the image to liked heart
                            if (document.getId().equals(currentGridItem.getId())) {
                                Glide.with(DetailsActivity.this).load(R.drawable.ic_liked_favourite).into(favouriteButton);
                                isLiked = true;
                            }
                        }
                    }
                }
            });
        }
    }


    private void initializeUI() {
        totalVotersTextView = findViewById(R.id.details_total_voters);
        totalRatingTextView = findViewById(R.id.rating_result_text_view);
        materialRatingBar = findViewById(R.id.details_rating_bar);
        readMoreTextView = findViewById(R.id.read_more_text_view);
        freeShippingLayout = findViewById(R.id.free_shipping);
        progressBar = findViewById(R.id.details_progress_bar);
        discountTextView = findViewById(R.id.discount_text_view);
        itemImageView = findViewById(R.id.item_image_view);
        brandTextView = findViewById(R.id.brand_text_view);
        titleTextView = findViewById(R.id.title_text_view);
        priceTextView = findViewById(R.id.price_text_view);
        oldPriceTextView = findViewById(R.id.crossed_price_text_view);
        deliveryInfoTextView = findViewById(R.id.delivery_info_text_view);
        returnPolicyTextView = findViewById(R.id.return_policy_text_view);
        warrantyTextView = findViewById(R.id.warranty_text_view);
        descriptionTextView = findViewById(R.id.description_details);
        productsRatings = findViewById(R.id.product_ratings);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        ratingLayout = findViewById(R.id.details_rating_layout);
        favouriteButton = findViewById(R.id.favourite_image);
        toolbar = findViewById(R.id.details_tool_bar);
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void setLikeButton() {

    }

    @Override
    public void setUnlikedButton() {

    }
}
