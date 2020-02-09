package com.example.market.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = ViewedItem.class , version = 1 , exportSchema = false)
public abstract class ViewedItemDatabase extends RoomDatabase {

    //we make this variable as we should change this class to a singleton which means only
    //one instance can be created and used for the whole app
    private static ViewedItemDatabase instance;

    //later we will use this dao method to access all the operations we declared in the dao interface
    //which we will do in the repository class
    public abstract ViewedItemDao viewedItemDao();

    public static synchronized ViewedItemDatabase getInstance(Context context){

        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext() ,
                    ViewedItemDatabase.class , "viewed_items_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
