package com.example.market.description;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.market.R;

import static com.example.market.details.ui.DetailsActivity.KEY_DESCRIPTION;
import static com.example.market.details.ui.DetailsActivity.KEY_FEATURES;

public class DescriptionActivity extends AppCompatActivity {

    private TextView descriptionTextView;
    private TextView keyFeaturesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        descriptionTextView = findViewById(R.id.description_text_view);
        keyFeaturesTextView = findViewById(R.id.key_features_text_view);
        Intent intent = getIntent();
        descriptionTextView.setText(intent.getStringExtra(KEY_DESCRIPTION));
        keyFeaturesTextView.setText(intent.getStringExtra(KEY_FEATURES));
    }
}
