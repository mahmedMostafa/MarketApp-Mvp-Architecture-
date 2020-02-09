package com.example.market.home;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.market.pojos.BestProductsItem;
import com.example.market.pojos.DealsItem;
import com.example.market.pojos.GridItem;
import com.example.market.pojos.LabelItem;
import com.example.market.pojos.PicturesItem;
import com.example.market.pojos.TopCategoryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter {

    private FirebaseFirestore fireStore;
    private StorageReference storageReference;
    private ArrayList<LabelItem> labelItems = new ArrayList<>();
    private ArrayList<DealsItem> deals = new ArrayList<>();
    private ArrayList<PicturesItem> picturesItems = new ArrayList<>();
    private ArrayList<TopCategoryItem> topCategoriesList = new ArrayList<>();
    private ArrayList<String> adsList = new ArrayList<>();
    private ArrayList<BestProductsItem> bestProductsItems = new ArrayList<>();

    private Context context;
    private HomeView homeView;

     HomePresenter(Context context, HomeView homeView) {
        this.context = context;
        this.homeView = homeView;
        storageReference = FirebaseStorage.getInstance().getReference();
        fireStore = FirebaseFirestore.getInstance();
        getAllLabels();
        getGridSales();
        getPictureItems();
        getTopCategories();
        getSliderImages();
        getBestSales();
    }

     ArrayList<LabelItem> getAllLabels() {


        //first we fill the array list with empty data
        for (int i = 0; i < 6; i++) {
            labelItems.add(new LabelItem("", ""));
        }
        final CollectionReference query = fireStore.collection("labels/");
        //final int i = 0;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int k = 0;
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    for (DocumentSnapshot document : docs) {
                        LabelItem item = labelItems.get(k++);
                        item.setTitle(document.get("title").toString());
                        item.setDescription(document.get("offer").toString());
                        item.setId(document.getId());
                        /*if(!document.get("offer").toString().equals("")){
                            dealsAdapter.setVisible();
                        }*/
                        //labelItems.add(item);
                        //dealsAdapter.notifyDataSetChanged();
                        homeView.notifyDealsAdapter();
                    }
                    Log.d("yes i'm here", "i'm here");
                } else {
                    Log.d("errorsa", "i'm here");
                }

            }
        });
        return labelItems;
    }

     ArrayList<DealsItem> getGridSales() {

        //filling the deals items with dummy data
        for (int j = 0; j < 6; j++) {
            ArrayList<GridItem> gridItems = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                gridItems.add(new GridItem("", "", "", ""));
            }
            DealsItem dealsItem = new DealsItem();
            dealsItem.setItemsList(gridItems);
            deals.add(dealsItem);
        }
        //int k=0;
        CollectionReference query = fireStore.collection("grid_items/");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (int i = 0; i < 6; i++) {
                        final int k = i;
                        DocumentSnapshot document = docs.get(i);

                        CollectionReference reference = document.getReference().collection("items/");
                        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                    final ArrayList<GridItem> items = deals.get(k).getItemsList();
                                    //final GridItem gridItem = items.get(k);
                                    //int y=0;
                                    for (int j = 0; j < 4; j++) {
                                        DocumentSnapshot document = docs.get(j);
                                        final GridItem gridItem = items.get(j);
                                        gridItem.setTitle(document.get("title").toString());
                                        gridItem.setPrice(document.get("price").toString());
                                        gridItem.setDiscount(document.get("discount").toString());
                                        gridItem.setId(document.getId());
                                        StorageReference mRef = storageReference.child("grid_sales/" + gridItem.getId() + ".jpg");
                                        mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                gridItem.setImageUrl(uri.toString());
                                                homeView.notifyDealsAdapter();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("", "the damn error is :" + e.getMessage());
                                                Log.d("gridItem id : ", gridItem.getId());
                                                //Toast.makeText(getActivity(), "the same damn error!!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
//                                    dealsAdapter.updateGridList(items);
//                                    DealsItem dealsItem = new DealsItem();
//                                    dealsItem.setItemsList(items);
//                                    deals.set(k, dealsItem);
//                                    dealsAdapter.notifyDataSetChanged();
                                    homeView.updateDealsAdapter(k, items);
                                }
                            }
                        });


                    }
                }
            }
        });
        return deals;
    }

     ArrayList<PicturesItem> getPictureItems() {
        int k = 2;
        int s = 1;

        for (int i = 0; i < 6; i++) {
            picturesItems.add(new PicturesItem("", ""));
        }
        for (int i = 0; i < 6; i++) {

            StorageReference mRef = storageReference.child("offers/" + s + ".jpg");
            final PicturesItem mItem = picturesItems.get(i);
            mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mItem.setFirstPicture(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("", "the error is :" + e.getMessage());
                }
            });
            StorageReference ref = storageReference.child("offers/" + k + ".jpg");
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mItem.setSecondPicture(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("", "the error is :" + e.getMessage());
                }
            });
            picturesItems.set(i, mItem);
            //dealsAdapter.notifyDataSetChanged();
            s += 2;
            k += 2;
        }
        return picturesItems;
    }

     ArrayList<TopCategoryItem> getTopCategories() {

        //first we add fake dummy empty data so that the items will appear and then we change the content with the retrieved data
        for (int i = 0; i < 8; i++) {
            topCategoriesList.add(new TopCategoryItem("", "", "", ""));
        }
        //getting a reference to the top categories collection
        CollectionReference query = fireStore.collection("top_categories/");
        //get all the documents inside the collection
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //get the QuerySnapShots and make a list of the documents inside
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    //iterate inside all the documents
                    int i = 0;
                    for (DocumentSnapshot document : docs) {
                        //make an object of the TopCategoryItem and set the title and id of that object
                        final TopCategoryItem item = topCategoriesList.get(i++);
                        item.setLabel(document.get("title").toString());
                        String currentItemId = document.getId();
                        item.setItemId(currentItemId);
                        item.setCategory(document.get("category").toString());
                        //get a reference of the associated image and the stored id
                        StorageReference mRef = storageReference.child("top_categories/" + item.getItemId() + ".png");
                        //get the url of the image
                        mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //and finally add it to the list
                                item.setImage(uri.toString());
                                //notify that the data has changed as it might take some time to load from the fire base
                                homeView.notifyTopCategoriesAdapter();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.getMessage();
                                e.printStackTrace();
                                topCategoriesList.add(item);
                                homeView.notifyTopCategoriesAdapter();
                            }
                        });
                    }

                }
            }
        });
        Log.d("size of top categories", String.valueOf(topCategoriesList.size()));
        return topCategoriesList;
    }

    //this method is retrieve all the images from the fire base
     ArrayList<String> getSliderImages() {

        for (int i = 0; i < 7; i++) {
            adsList.add("");
        }

        StorageReference re = storageReference.child("slider_images/1.jpg");
        re.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                adsList.set(6, uri.toString());
                //dealsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                homeView.sliderError();
            }
        });

        StorageReference ref = storageReference.child("slider_images/" + "5.jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                adsList.set(0, uri.toString());
                //dealsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                homeView.sliderError();
            }
        });

        //i called them all with numbers from 1 to 5 so i can retrieve them with for loop
        for (int i = 1; i <= 5; i++) {
            StorageReference mRef = storageReference.child("slider_images/" + i + ".jpg");
            final int finalI = i;
            mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //adsList.add(uri.toString());
                    adsList.set(finalI, uri.toString());
                    //dealsAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    homeView.sliderError();
                }
            });
        }
        return adsList;
    }

     ArrayList<BestProductsItem> getBestSales() {

        for (int i = 0; i < 5; i++) {
            bestProductsItems.add(new BestProductsItem("", "", "", ""));
        }
        //z is the label id for the best sales
        //we named it like that so that it wouldn't bel loaded in the deals recyclerview
        CollectionReference collectionReference = fireStore.collection("grid_items/").document("z")
                .collection("items/");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    assert querySnapshot != null;
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();

                    int i = 0;
                    for (DocumentSnapshot document : docs) {

                        final BestProductsItem item = bestProductsItems.get(i++);
                        item.setBestProductsTitle(document.get("title").toString());
                        item.setBestProductsOriginalPrice(document.get("price").toString());
                        item.setBestProductsCrossedPrice(document.get("oldPrice").toString());
                        item.setBestProductsID(document.getId());

                        StorageReference mRef = storageReference.child("best_sales/" + document.getId() + ".jpg");
                        mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                item.setBestProductsImage(uri.toString());
                                //bestProductsItems.add(item);
                                homeView.notifyBestProductsAdapter();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("BEST PRODUCTS ERROR : ", e.getMessage());
                               homeView.bestProductsError();
                            }
                        });
                    }
                }
            }
        });
        return bestProductsItems;
    }

    public ArrayList<LabelItem> getLabelList(){
         return labelItems;
    }
    public ArrayList<DealsItem> getDealsList(){
        return deals;
    }
    public ArrayList<PicturesItem> getPicturesList(){
        return picturesItems;
    }
    public ArrayList<String> getAdsList(){
        return adsList;
    }

    public ArrayList<BestProductsItem> getBestProductsList(){
        return bestProductsItems;
    }

    public ArrayList<TopCategoryItem> getTopCategoriesList(){
        return topCategoriesList;
    }

}
