package com.kulikov.smart_fin;

import android.app.Application;

import androidx.room.Room;

import com.kulikov.smart_fin.db.SmatrfinDaoDatabase;


public class SmatrfinApp extends Application {
    public static SmatrfinApp instance;
    private SmatrfinDaoDatabase smatrfinDaoDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        smatrfinDaoDatabase = Room.databaseBuilder(getApplicationContext(), SmatrfinDaoDatabase.class, "DaoDatabase_db").
                // .allowMainThreadQueries().
                build();
    }

    public SmatrfinDaoDatabase getSmatrfinDatabase() {
        return smatrfinDaoDatabase;
    }

    public static SmatrfinApp getInstance() {
        return instance;
    }
}




