package com.example.market.feedback;

import com.example.market.pojos.GridItem;

public interface FeedbackView {

    void goToLogin();
    void errorMessage(String errorMessage);
    void openDialog();
    void updateReview();
    void hideNoRatingLayout();
    void updateAdapter();
    void updatedRating();
    void setRatingColor(GridItem gridItem);
    void toastMessage(String message);
    void dismissDialog();
    void hideRatingLayout();
    void setReviewInfo(String title,String description ,float rating);
    void updateReviewText();

}
