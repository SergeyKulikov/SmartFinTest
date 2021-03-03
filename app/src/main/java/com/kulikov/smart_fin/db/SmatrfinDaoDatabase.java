package com.kulikov.smart_fin.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { Cart.class, CartItem.class, CategoryItem.class, ProductItem.class }, version = 1, exportSchema = false)

public abstract class SmatrfinDaoDatabase extends RoomDatabase {
    public abstract SmatrfinDao getDaoDatabase();

}
