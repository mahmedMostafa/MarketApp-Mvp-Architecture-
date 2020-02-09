package com.example.market.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ViewedItemDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ViewedItem item);

    //we don't need update anyways
    @Update
    void update(ViewedItem item);


    @Delete
    void delete(ViewedItem item);

    @Query("DELETE FROM recently_viewed_items")
    void deleteAllItems();

    @Query("SELECT * FROM recently_viewed_items")
    LiveData<List<ViewedItem>> getAllViewedItems();
}
