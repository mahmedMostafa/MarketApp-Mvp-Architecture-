package com.example.market.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewedItemRepository {

    private ViewedItemDao viewedItemDao;

    private LiveData<List<ViewedItem>> allItems;

    public ViewedItemRepository(Application application){
        ViewedItemDatabase database = ViewedItemDatabase.getInstance(application);
        viewedItemDao = database.viewedItemDao();
        allItems = viewedItemDao.getAllViewedItems();
    }


    public void insert(ViewedItem item){
        new InsertItemAsyncTask(viewedItemDao).execute(item);
    }

    public void update(ViewedItem item){
        new UpdateItemAsyncTask(viewedItemDao).execute(item);
    }

    public void delete(ViewedItem item){
        new DeleteItemAsyncTask(viewedItemDao).execute(item);
    }

    public void deleteAllItems(){
        new DeleteAllItemAsyncTask(viewedItemDao).execute();
    }

    public LiveData<List<ViewedItem>> getAllItems(){
        return allItems;
    }


    private static class InsertItemAsyncTask extends AsyncTask<ViewedItem , Void , Void> {

        private ViewedItemDao dao;

        private InsertItemAsyncTask(ViewedItemDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ViewedItem... viewedItems) {
            dao.insert(viewedItems[0]);
            return null;
        }
    }

    private static class UpdateItemAsyncTask extends AsyncTask<ViewedItem , Void , Void> {

        private ViewedItemDao dao;

        private UpdateItemAsyncTask(ViewedItemDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ViewedItem... viewedItems) {
            dao.update(viewedItems[0]);
            return null;
        }
    }

    private static class DeleteItemAsyncTask extends AsyncTask<ViewedItem , Void , Void> {

        private ViewedItemDao dao;

        private DeleteItemAsyncTask(ViewedItemDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ViewedItem... viewedItems) {
            dao.delete(viewedItems[0]);
            return null;
        }
    }

    private static class DeleteAllItemAsyncTask extends AsyncTask<Void , Void , Void> {

        private ViewedItemDao dao;

        private DeleteAllItemAsyncTask(ViewedItemDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllItems();
            return null;
        }
    }
}
