package com.example.market.feedback;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class FeedbackPresenter {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseFirestore firebaseFirestore;
    String buyerName;
    private boolean hasReviewed = false;
    private GridItem currentGridItem;
    private ArrayList<Review> reviews = new ArrayList<>();
    private String label_id;
    private int oldRating;

    private Context context;
    private FeedbackView view;

    public FeedbackPresenter(Context context, FeedbackView view , String labelId , GridItem currentGridItem) {
        this.context = context;
        this.view = view;
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        label_id = labelId;
        this.currentGridItem = currentGridItem;
    }

    public boolean isHasReviewed(){
        return hasReviewed;
    }

    public void setOldRating(int oldRating){
        this.oldRating = oldRating;
    }
    public int getOldRating(){
        return oldRating;
    }

    public void setLabelId(String label_id){
        this.label_id = label_id;
    }

    public void checkUser() {
        //final String name="";
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
//                    startActivity(new Intent(FeedbackActivity.this, LoginActivity.class));
//                    Toast.makeText(FeedbackActivity.this, "You must Login to add your review", Toast.LENGTH_LONG).show();
                    view.goToLogin();
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("users/").document(firebaseAuth.getCurrentUser().getUid());
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                //Toast.makeText(FeedbackActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                view.errorMessage("Something went wrong!");
                            } else {
                                buyerName = documentSnapshot.get("first_name").toString();
                                /*Intent intent = new Intent(FeedbackActivity.this,AddReviewActivity.class);
                                intent.putExtra(KEY_LABEL_ID,label_id);
                                intent.putExtra(KEY_GRID_ID,currentGridItem.getId());
                                intent.putExtra(KEY_FIRST_NAME,buyerName);
                                startActivity(intent);*/
                                view.openDialog();
                            }
                        }
                    });
                }
            }
        };
        mAuth.addAuthStateListener(listener);
    }

    public void hasReviewed() {
        if(mAuth.getCurrentUser() != null){
            firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                    .collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot document : docs) {
                        if (document.getId().equals(currentGridItem.getId())) {
                            hasReviewed = true;
                            //reviewTextView.setText("Update Your Review");
                            view.updateReviewText();
                            view.updateReview();
                        }
                    }
                }
            });
        }

    }

    void onStop(){
        if (mAuth != null) {
            mAuth.removeAuthStateListener(listener);
        }
    }

    public void getAllReviews() {
        reviews.clear();
        CollectionReference reference = firebaseFirestore.collection("grid_items/").document(label_id).collection("items/")
                .document(currentGridItem.getId()).collection("reviews/");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error is :" + e.getMessage());
                    view.errorMessage(e.getMessage());
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
                                //noRatingsLayout.setVisibility(View.GONE);
                                view.hideNoRatingLayout();
                            case MODIFIED:

                            case REMOVED:
                        }
                    }
//                    adapter = new FeedBackAdapter(reviews, FeedbackActivity.this);
//                    Log.d("", "size of reviews is :" + reviews.size());
//                    recyclerView.setAdapter(adapter);
                    view.updateAdapter();
                }
            }
        });

    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    //i'm not sure but this maybe only works in egypt
    //review the time zone
     String getCurrentDate() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }

    void updateRating(Review review, final GridItem gridItem) {

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
               // Toast.makeText(FeedbackActivity.this, "Successfully update Rating", Toast.LENGTH_SHORT).show();
                view.updatedRating();
                //currentGridItem = rate;
                currentGridItem = rate;
                //setRatingColor(currentGridItem);
                view.setRatingColor(currentGridItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

     void addReview(String title,String description , float rating){
//            ratingLayout.setVisibility(View.VISIBLE);
//            dialog.dismiss();
            //view.hideRatingLayout();
            view.dismissDialog();
            Review review = new Review();
            review.setTitle(title);
            review.setDescription(description);
            review.setTimeStamp(getCurrentDate());
            review.setRating(rating);
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
                    view.toastMessage("Reviews added to user!!");
                    hasReviewed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("review error is :", e.getMessage());
                    view.errorMessage(e.getMessage());
                }
            });
            //view.openDialog();
    }

    private void insertReview(final Review review) {
        Review review1 = new Review(review.getTitle(), review.getDescription(), review.getBuyer(), review.getRating(), review.getTimeStamp());

        CollectionReference reference = firebaseFirestore.collection("grid_items/").document(label_id).collection("items/")
                .document(currentGridItem.getId()).collection("reviews/");

        reference.document(mAuth.getCurrentUser().getUid()).set(review1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.toastMessage("Rating posted!!");
                        updateRating(review, currentGridItem);
                        view.setRatingColor(currentGridItem);
                        //setRatingColor(currentGridItem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                view.toastMessage("Rating not inserted !!");
            }
        });
    }

     void getSubmittedRating(){
        firebaseFirestore.collection("users/").document(mAuth.getCurrentUser().getUid())
                .collection("reviews/").document(currentGridItem.getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            view.errorMessage("Something wend wrong!");
                        } else {
//                            title.setText(documentSnapshot.get("title").toString());
//                            description.setText(documentSnapshot.get("review").toString());
//                            ratingBar.setRating(Float.parseFloat(documentSnapshot.get("rating").toString()));
//                            oldRating =(int) Float.parseFloat(documentSnapshot.get("rating").toString());
                            view.setReviewInfo(documentSnapshot.get("title").toString(),
                                    documentSnapshot.get("review").toString(),
                                    Float.parseFloat(documentSnapshot.get("rating").toString()));
                        }
                    }
                });
        //view.openDialog();
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

    void sendReview(String title,String description , float rating ){
        Review review = new Review();
        review.setTitle(title);
        review.setDescription(description);
        review.setTimeStamp(getCurrentDate());
        review.setRating(rating);
        updateStarRating(oldRating);
        updateReview(review);
        //dialog.dismiss();
        view.dismissDialog();
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
