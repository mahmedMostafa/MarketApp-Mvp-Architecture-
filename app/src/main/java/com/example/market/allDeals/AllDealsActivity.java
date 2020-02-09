package com.example.market.allDeals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market.details.ui.DetailsActivity;
import com.example.market.home.adapters.AllDealsAdapter;
import com.example.market.pojos.AllDealsItem;
import com.example.market.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.market.main.MainActivity.KEY_GRID_ID;
import static com.example.market.main.MainActivity.KEY_LABEL_ID;

public class AllDealsActivity extends AppCompatActivity {

    private RecyclerView allDealsRecyclerView;
    private AllDealsAdapter allDealsAdapter;
    private List<AllDealsItem> allDealsItems;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private String category;
    private ArrayList<String> userLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_deals);
        allDealsItems = new ArrayList<>();
        userLikes = new ArrayList<>();
        allDealsRecyclerView = findViewById(R.id.all_deals_recycler_view);
        allDealsRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        allDealsRecyclerView.setHasFixedSize(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        getAllDeals();
    }


    private void getAllDeals(){
        Intent intent = getIntent();
        category = intent.getStringExtra(KEY_LABEL_ID);
        Log.e("category",category);
        firebaseFirestore.collection("grid_items/").document(category).collection("items/")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!(task.isSuccessful())){
                    Toast.makeText(AllDealsActivity.this, "Couldn't load data!!", Toast.LENGTH_SHORT).show();
                }else{
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for(DocumentSnapshot document : docs){
                       /* AllDealsItem allDealsItem = document.toObject(AllDealsItem.class);
                        allDealsItems.add(allDealsItem);*/
                       AllDealsItem item = new AllDealsItem();
                       item.setDealsBrand(document.get("brand").toString());
                       Log.e("item brand",item.getDealsBrand());
                       item.setDealsDiscount(document.get("discount").toString());
                       item.setDealsFreeShipping(Boolean.parseBoolean(document.get("freeShipping").toString()));
                       item.setDealsImageUrl(document.get("imageUrl").toString());
                       item.setDealsTitle(document.get("title").toString());
                       item.setDealsPrice(document.get("price").toString());
                       item.setDealsOldPrice(document.get("oldPrice").toString());
                       item.setDealsRating(Double.parseDouble(document.get("totalRating").toString()));
                       item.setDealsId(document.getId());
                       allDealsItems.add(item);

                    }
                    Log.e("list size :" , String.valueOf(allDealsItems.size()));
                    allDealsAdapter = new AllDealsAdapter(allDealsItems,AllDealsActivity.this);
                    allDealsRecyclerView.setAdapter(allDealsAdapter);
                    //allDealsAdapter.notifyDataSetChanged();
                    allDealsAdapter.setOnItemClickListener(new AllDealsAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int position) {
                            Intent intent = new Intent(AllDealsActivity.this, DetailsActivity.class);
                            intent.putExtra(KEY_LABEL_ID,category);
                            intent.putExtra(KEY_GRID_ID,allDealsItems.get(position).getDealsId());
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
