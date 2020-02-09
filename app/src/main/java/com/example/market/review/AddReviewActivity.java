package com.example.market.review;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.market.pojos.Review;
import com.example.market.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.example.market.feedback.FeedbackActivity.KEY_FIRST_NAME;
import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class AddReviewActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button postReviewButton;
    private FirebaseFirestore firebaseFirestore;
    private MaterialRatingBar ratingBar;
    private String label_id, grid_id, title, description,name;
    private int totalStarsRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        firebaseFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_add_review);
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        postReviewButton = findViewById(R.id.post_review_button);
        ratingBar = findViewById(R.id.total_star_rating);


        Intent intent = getIntent();
        label_id = intent.getStringExtra(KEY_LABEL_ID);
        grid_id = intent.getStringExtra(KEY_GRID_ID);
        name = intent.getStringExtra(KEY_FIRST_NAME);

        postReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
                finish();
            }
        });
    }


    private void addReview() {

        totalStarsRating = Math.round(ratingBar.getRating());
        title = titleEditText.getText().toString().trim();
        description = descriptionEditText.getText().toString().trim();
        CollectionReference reference = firebaseFirestore.collection("grid_items/").document(label_id).collection("items/")
                .document(grid_id).collection("reviews/");
       /* Log.d("","label_id is :" + label_id);
        Log.d("","grid_id is :" + grid_id);*/

        final Review review = new Review();
        review.setTitle(title);
        review.setDescription(description);
        review.setBuyer(name);
        review.setRating(totalStarsRating);
        review.setTimeStamp(getCurrentDate());

        reference.document().set(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddReviewActivity.this, "Review Added Successfully!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("","Error is :" + e.getMessage());
                Toast.makeText(AddReviewActivity.this, "Couldn't add review!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //i'm not sure but this maybe only works in egypt
    //review the time zone
    private String getCurrentDate(){
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd",calendar).toString();
        return date;
    }


}
