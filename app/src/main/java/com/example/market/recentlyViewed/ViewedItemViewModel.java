package com.example.market.recentlyViewed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.market.database.ViewedItem;
import com.example.market.database.ViewedItemRepository;

import java.util.List;

public class ViewedItemViewModel extends AndroidViewModel {

    private ViewedItemRepository repository;
    private LiveData<List<ViewedItem>> allItems;

    public ViewedItemViewModel(@NonNull Application application) {
        super(application);
        repository = new ViewedItemRepository(application);
        allItems = repository.getAllItems();
    }

    public void insert(ViewedItem item){
        repository.insert(item);
    }

    public void update(ViewedItem item){
        repository.update(item);
    }

    public void delete(ViewedItem item){
        repository.delete(item);
    }

    public LiveData<List<ViewedItem>> getAllItems(){
        return allItems;
    }

    public void deleteAllItems(){
        repository.deleteAllItems();
    }
}
