package com.example.market.feedback;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.Adapters.FeedBackAdapter;
import com.example.market.R;
import com.example.market.auth.login.LoginActivity;
import com.example.market.pojos.GridItem;
import com.example.market.pojos.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class FeedbackActivity extends AppCompatActivity {

    public static final String KEY_FIRST_NAME = "first_name";
    private RelativeLayout addReviewLayout;
    private RelativeLayout noRatingsLayout;
    private RecyclerView recyclerView;
    private FeedBackAdapter adapter;
    private ArrayList<Review> reviews;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseFirestore firebaseFirestore;
    private String label_id;
    private GridItem currentGridItem;
    private TextView reviewTextView;
    private boolean hasReviewed = false;
    private LinearLayout ratingLayout;
    private int oldRating;
    private TextView averageRating, totalVotersTextView;
    MaterialRatingBar ratingBar;
    private ConstraintLayout constraintLayout1, constraintLayout2, constraintLayout3, constraintLayout4, constraintLayout5;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5;

    String buyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_feedback);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        label_id = intent.getStringExtra(KEY_LABEL_ID);
        //grid_id = intent.getStringExtra(KEY_GRID_ID);

        //get the whole item from the details activity (isn't it cool!! :D)
        currentGridItem = new Gson().fromJson(intent.getStringExtra(KEY_GRID_ID), GridItem.class);


        initUI();

        addReviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
        reviews = new ArrayList<>();
        recyclerView = findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                setRatingColor(currentGridItem);
                getAllReviews();
            }
        },500);*/

        //because the constraint layout doesn't get instantiated fast so we add a layout removeListener
        constraintLayout1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constraintLayout1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setRatingColor(currentGridItem);
                getAllReviews();
            }
        });
        hasReviewed();
    }

    private void initUI() {
        ratingLayout = findViewById(R.id.rating_layout);
        addReviewLayout = findViewById(R.id.add_review_layout);
        noRatingsLayout = findViewById(R.id.no_ratings_layout);
        constraintLayout1 = findViewById(R.id.constrain_layout_1);
        constraintLayout2 = findViewById(R.id.constrain_layout_2);
        constraintLayout2 = findViewById(R.id.constrain_layout_3);
        constraintLayout2 = findViewById(R.id.constrain_layout_4);
        constraintLayout2 = findViewById(R.id.constrain_layout_5);
        linearLayout1 = findViewById(R.id.ll_percentage_1);
        linearLayout2 = findViewById(R.id.ll_percentage_2);
        linearLayout3 = findViewById(R.id.ll_percentage_3);
        linearLayout4 = findViewById(R.id.ll_percentage_4);
        linearLayout5 = findViewById(R.id.ll_percentage_5);
        reviewTextView = findViewById(R.id.review_text_view);
        averageRating = findViewById(R.id.average_rating_text_view);
        totalVotersTextView = findViewById(R.id.total_voters_text_view);
        ratingBar = findViewById(R.id.total_bar_rating);
    }

    private void hasReviewed() {
        firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                .collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                for (DocumentSnapshot document : docs) {
                    if (document.getId().equals(currentGridItem.getId())) {
                        hasReviewed = true;
                        reviewTextView.setText("Update Your Review");
                    }
                }
            }
        });
    }

    private void checkUser() {
        //final String name="";
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(FeedbackActivity.this, LoginActivity.class));
                    Toast.makeText(FeedbackActivity.this, "You must Login to add your review", Toast.LENGTH_LONG).show();
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("users/").document(firebaseAuth.getCurrentUser().getUid());
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(FeedbackActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            } else {
                                buyerName = documentSnapshot.get("first_name").toString();
                                /*Intent intent = new Intent(FeedbackActivity.this,AddReviewActivity.class);
                                intent.putExtra(KEY_LABEL_ID,label_id);
                                intent.putExtra(KEY_GRID_ID,currentGridItem.getId());
                                intent.putExtra(KEY_FIRST_NAME,buyerName);
                                startActivity(intent);*/
                                openDialogReview();
                            }
                        }
                    });
                }
            }
        };
        mAuth.addAuthStateListener(listener);
    }

    private void getAllReviews() {
        reviews.clear();
        CollectionReference reference = firebaseFirestore.collection("grid_items/").document(label_id).collection("items/")
                .document(currentGridItem.getId()).collection("reviews/");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error is :" + e.getMessage());
                    Toast.makeText(FeedbackActivity.this, "Error is :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (queryDocumentSnapshots.getDocumentChanges().size() == 0) {
                            break;
                        }
                        switch (doc.getType()) {
                            case ADDED:
                                Review review = new Review();
                                review.setTitle(doc.getDocument().get("title").toString());
                                review.setDescription(doc.getDocument().get("description").toString());
                                review.setBuyer(doc.getDocument().get("buyer").toString());
                                if (doc.getDocument().get("timeStamp") == null) {
                                    review.setTimeStamp("Today");
                                } else {
                                    review.setTimeStamp(doc.getDocument().get("timeStamp").toString());
                                }
                                review.setRating(Double.parseDouble(doc.getDocument().get("rating").toString()));
                                //review.setTimeStamp(doc.getDocument().get("timestamp").toString());
                                reviews.add(review);
                                //updateRating(review,currentGridItem);
                                //adapter.notifyDataSetChanged();
                                noRatingsLayout.setVisibility(View.GONE);
                            case MODIFIED:

                            case REMOVED:
                        }
                    }
                    adapter = new FeedBackAdapter(reviews, FeedbackActivity.this);
                    Log.d("", "size of reviews is :" + reviews.size());
                    recyclerView.setAdapter(adapter);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(removeListener);
        //setRatingColor(currentGridItem);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(listener);
        }
    }

    private void updateRating(Review review, final GridItem gridItem) {

        final GridItem rate = new GridItem();
        rate.setId(gridItem.getId());
        rate.setTitle(gridItem.getTitle());
        rate.setKeyFeatures(gridItem.getKeyFeatures());
        rate.setDescription(gridItem.getDescription());
        rate.setFreeShipping(gridItem.isFreeShipping());
        rate.setDiscount(gridItem.getDiscount());
        rate.setWarranty(gridItem.getWarranty());
        rate.setDeliveryInfo(gridItem.getDeliveryInfo());
        rate.setOldPrice(gridItem.getOldPrice());
        rate.setPrice(gridItem.getPrice());
        rate.setBrand(gridItem.getBrand());
        rate.setReturnPolicy(gridItem.getReturnPolicy());
        rate.setImageUrl(gridItem.getImageUrl());

        double totalStars = 0;
        int totalVoters = 0;

        if (review.getRating() == 1.0) {
            totalStars = 1.0 + (double) gridItem.getStar1();
            rate.setStar1((int) totalStars);
            rate.setStar2(gridItem.getStar2());
            rate.setStar3(gridItem.getStar3());
            rate.setStar4(gridItem.getStar4());
            rate.setStar5(gridItem.getStar5());

            totalVoters = (int) (totalStars + gridItem.getStar2() + gridItem.getStar3() + gridItem.getStar4() + gridItem.getStar5());
            if (gridItem.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (review.getRating() == 2.0) {
            totalStars = 1.0 + (double) gridItem.getStar2();
            rate.setStar1(gridItem.getStar1());
            rate.setStar2((int) totalStars);
            rate.setStar3(gridItem.getStar3());
            rate.setStar4(gridItem.getStar4());
            rate.setStar5(gridItem.getStar5());

            totalVoters = (int) (gridItem.getStar1() + totalStars + gridItem.getStar3() + gridItem.getStar4() + gridItem.getStar5());
            if (gridItem.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (review.getRating() == 3.0) {
            totalStars = 1.0 + (double) gridItem.getStar3();
            rate.setStar1(gridItem.getStar1());
            rate.setStar2(gridItem.getStar2());
            rate.setStar3((int) totalStars);
            rate.setStar4(gridItem.getStar4());
            rate.setStar5(gridItem.getStar5());

            totalVoters = (int) (gridItem.getStar1() + gridItem.getStar2() + totalStars + gridItem.getStar4() + gridItem.getStar5());
            if (gridItem.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (review.getRating() == 4.0) {
            totalStars = 1.0 + (double) gridItem.getStar4();
            rate.setStar1(gridItem.getStar1());
            rate.setStar2(gridItem.getStar2());
            rate.setStar3(gridItem.getStar3());
            rate.setStar4((int) totalStars);
            rate.setStar5(gridItem.getStar5());

            totalVoters = (int) (gridItem.getStar1() + gridItem.getStar2() + gridItem.getStar3() + totalStars + gridItem.getStar5());
            if (gridItem.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        } else if (review.getRating() == 5.0) {
            totalStars = 1.0 + (double) gridItem.getStar5();
            rate.setStar1(gridItem.getStar1());
            rate.setStar2(gridItem.getStar2());
            rate.setStar3(gridItem.getStar3());
            rate.setStar4(gridItem.getStar4());
            rate.setStar5((int) totalStars);

            totalVoters = (int) (gridItem.getStar1() + gridItem.getStar2() + gridItem.getStar3() + gridItem.getStar4() + totalStars);
            if (gridItem.getTotalVoters() == 0) {
                rate.setTotalVoters(1);
            } else {
                rate.setTotalVoters(totalVoters);
            }
        }


        int totalStars1 = rate.getStar1();
        int totalStar2 = rate.getStar2() * 2;
        int totalStar3 = rate.getStar3() * 3;
        int totalStar4 = rate.getStar4() * 4;
        int totalStar5 = rate.getStar5() * 5;


        double sumOfStars = totalStars1 + totalStar2 + totalStar3 + totalStar4 + totalStar5;
        double totalRating = sumOfStars / (double) totalVoters;
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        rate.setTotalRating(Double.parseDouble(decimalFormat.format(totalRating)));

        DocumentReference reference = firebaseFirestore.collection("grid_items/")
                .document(label_id).collection("items/").document(gridItem.getId());

       /* Map<String,Object> rating = new HashMap<>();
        rating.put("totalRating" , rate.getTotalRating());
        rating.put("totalVotersTextView" , rate.getTotalVoters());
        rating.put("star1",rate.getStar1());
        rating.put("star2",rate.getStar2());
        rating.put("star3",rate.getStar3());
        rating.put("star4",rate.getStar4());
        rating.put("star5",rate.getStar5());*/
        /*averageRating.setText(String.valueOf(decimalFormat.format(totalRating)));
        ratingBar.setDealsRating(Float.parseFloat(String.valueOf(totalRating)));
        totalVotersTextView.setText(String.valueOf(totalVoters));*/


        reference.set(rate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FeedbackActivity.this, "Successfully update Rating", Toast.LENGTH_SHORT).show();
                //currentGridItem = rate;
                currentGridItem = rate;
                setRatingColor(currentGridItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void insertReview(final Review review) {
        Review review1 = new Review(review.getTitle(), review.getDescription(), review.getBuyer(), review.getRating(), review.getTimeStamp());

        CollectionReference reference = firebaseFirestore.collection("grid_items/").document(label_id).collection("items/")
                .document(currentGridItem.getId()).collection("reviews/");

        reference.document(mAuth.getCurrentUser().getUid()).set(review1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FeedbackActivity.this, "Rating posted!!", Toast.LENGTH_SHORT).show();
                        updateRating(review, currentGridItem);
                        //setRatingColor(currentGridItem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FeedbackActivity.this, "Rating not inserted !!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
    this method is used to display the rating and the rating progress color
    */
    private void setRatingColor(GridItem gridItem) {

        int width = constraintLayout1.getWidth();
        int totalVoters = gridItem.getTotalVoters();
        int totalStar1Voters = gridItem.getStar1();
        int totalRateStar2 = gridItem.getStar2();
        int totalRateStar3 = gridItem.getStar3();
        int totalRateStar4 = gridItem.getStar4();
        int totalRateStar5 = gridItem.getStar5();

        //Log.d("","gridItem star1 :" + gridItem.getStar1());
        //Log.d("","gridItem totalVoters:" + gridItem.getTotalVoters());
        //Log.d("","gridItem star4:" + gridItem.getStar4());
        Log.d("", "totalVoters : " + totalVoters);
        Log.d("", "layout width is  : " + width);

        double votersInDouble = (double) totalVoters;

        //Rating star 1
        double star1 = (double) totalStar1Voters;
        double sum = (star1 / votersInDouble);
        int rating1 = (int) (sum * width);

        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0, 5, 0, 5);
        linearLayout1.setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.colorPrimaryDark));
        linearLayout1.setLayoutParams(layoutParams1);

        /*Log.d("","gridItem star1 :" + gridItem.getStar1());
        Log.d("","gridItem star1 sum :" + sum);
        Log.d("","rating of star1 is :" + rating1);*/

        //Rating star 2
        double star2 = (double) totalRateStar2;
        double sum2 = (star2 / votersInDouble);
        int rating2 = (int) (sum2 * width);
        //Log.d("","rating of star1 is :" + rating2);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams2.setMargins(0, 5, 0, 5);
        linearLayout2.setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.colorPrimaryDark));
        linearLayout2.setLayoutParams(layoutParams2);

        //Rating star 3
        double star3 = (double) totalRateStar3;
        double sum3 = (star3 / votersInDouble);
        int rating3 = (int) (sum3 * width);
        //Log.d("","rating of star1 is :" + rating3);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams3.setMargins(0, 5, 0, 5);
        linearLayout3.setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.colorPrimaryDark));
        linearLayout3.setLayoutParams(layoutParams3);

        //Rating star 4
        double star4 = (double) totalRateStar4;
        double sum4 = (star4 / votersInDouble);
        int rating4 = (int) (sum4 * width);
        //Log.d("","rating of star1 is :" + rating4);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams4.setMargins(0, 5, 0, 5);
        linearLayout4.setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.colorPrimaryDark));
        linearLayout4.setLayoutParams(layoutParams4);

        //Rating star 5
        double star5 = (double) totalRateStar5;
        double sum5 = (star5 / votersInDouble);
        int rating5 = (int) (sum5 * width);
        //Log.d("","rating of star1 is :" + rating5);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams5.setMargins(0, 5, 0, 5);
        linearLayout5.setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.colorPrimaryDark));
        linearLayout5.setLayoutParams(layoutParams5);


        int totalStar1 = totalStar1Voters;
        int totalStar2 = totalRateStar2 * 2;
        int totalStar3 = totalRateStar3 * 3;
        int totalStar4 = totalRateStar4 * 4;
        int totalStar5 = totalRateStar5 * 5;


        double sumRating = totalStar1 + totalStar2 + totalStar3 + totalStar4 + totalStar5;

        double rating = (sumRating / votersInDouble);
        DecimalFormat format = new DecimalFormat(".#");


        //Log.d("","voters count is :" + votersInDouble);
        //Log.d("","sum rating is :" + sumRating);
        //Log.d("","average rating is :" + String.valueOf(format.format(rating)));
        if (String.valueOf(format.format(rating)).equals("NaN")) {
            ratingLayout.setVisibility(View.GONE);
            averageRating.setText("0.0");
        } else {
            averageRating.setText(String.valueOf(format.format(rating)));
        }
        ratingBar.setRating(Float.parseFloat(String.valueOf(rating)));
        totalVotersTextView.setText(String.valueOf(totalVoters));
        Toast.makeText(this, "Im' here", Toast.LENGTH_SHORT).show();
    }

    //i'm not sure but this maybe only works in egypt
    //review the time zone
    private String getCurrentDate() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }

    private void openDialogReview() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review);


        final EditText title = dialog.findViewById(R.id.et_name);
        final EditText description = dialog.findViewById(R.id.et_review);
        Button sendReview = dialog.findViewById(R.id.btn_send_review);
        final MaterialRatingBar ratingBar = dialog.findViewById(R.id.rate_star);
        if (!hasReviewed) {
            sendReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingLayout.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    Review review = new Review();
                    review.setTitle(title.getText().toString().trim());
                    review.setDescription(description.getText().toString().trim());
                    review.setTimeStamp(getCurrentDate());
                    review.setRating(ratingBar.getRating());
                    review.setBuyer(buyerName);
                    insertReview(review);
                    String id = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("users/").document(id).collection("reviews")
                            .document(currentGridItem.getId());

                    Map<String, Object> map = new HashMap<>();
                    map.put("title", review.getTitle());
                    map.put("review", review.getDescription());
                    map.put("rating", review.getRating());
                    documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(FeedbackActivity.this, "Reviews added to user!!", Toast.LENGTH_SHORT).show();
                            hasReviewed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("review error is :", e.getMessage());
                            Toast.makeText(FeedbackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                /*
                updateRating(review,currentGridItem);
                setRatingColor(currentGridItem);
                */
                }
            });

            dialog.show();
        } else {
            firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                    .collection("reviews/").document(currentGridItem.getId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(FeedbackActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            } else {
                                title.setText(documentSnapshot.get("title").toString());
                                description.setText(documentSnapshot.get("review").toString());
                                ratingBar.setRating(Float.parseFloat(documentSnapshot.get("rating").toString()));
                                oldRating =(int) Float.parseFloat(documentSnapshot.get("rating").toString());
                            }
                        }
                    });

            sendReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Review review = new Review();
                    review.setTitle(title.getText().toString().trim());
                    review.setDescription(description.getText().toString().trim());
                    review.setTimeStamp(getCurrentDate());
                    review.setRating(ratingBar.getRating());
                    updateStarRating(oldRating);
                    updateReview(review);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    private void updateReview(Review review){
        //update the review that the item itself has
        DocumentReference documentReference =  firebaseFirestore.collection("grid_items/").document(label_id)
                .collection("items").document(currentGridItem.getId())
                .collection("reviews/").document(mAuth.getCurrentUser().getUid());
        documentReference.update("title",review.getTitle());
        documentReference.update("description",review.getDescription());
        documentReference.update("rating",review.getRating());
        documentReference.update("timeStamp",review.getTimeStamp());

        //update the review that the user has
        DocumentReference document = firebaseFirestore.collection("users/")
                .document(mAuth.getCurrentUser().getUid()).collection("reviews/")
                .document(currentGridItem.getId());
        document.update("title",review.getTitle());
        document.update("review",review.getDescription());
        document.update("rating",review.getRating());
        //currentGridItem.setTotalVoters(currentGridItem.getTotalVoters() - 2 );
        updateRating(review,currentGridItem);
        getAllReviews();
        /*Intent intent = getIntent();
        finish();
        startActivity(intent);*/
    }

    private void updateStarRating(int oldRating){
        if(oldRating == 1){
            currentGridItem.setStar1(currentGridItem.getStar1() - 1);
        }else if(oldRating == 2){
            currentGridItem.setStar2(currentGridItem.getStar2() -1);
        }else if(oldRating == 3){
            currentGridItem.setStar3(currentGridItem.getStar3() -1);
        }else if(oldRating == 4){
            currentGridItem.setStar4(currentGridItem.getStar4() -1);
        }else if (oldRating == 5){
            currentGridItem.setStar5(currentGridItem.getStar5() -1);
        }
    }

}
