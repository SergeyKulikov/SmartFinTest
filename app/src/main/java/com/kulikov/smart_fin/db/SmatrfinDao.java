package com.kulikov.smart_fin.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SmatrfinDao {
    // Названия грузим из нового списка, если что-то поменялось
    @Query("SELECT * FROM CartItem WHERE receipt_id = :receipt_id")
    List<CartItem> loadCartItems(Long receipt_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCartItem(CartItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveCartItems(List<CartItem> items);

    @Delete()
    void DeleteCartItems(List<CartItem> path);

    @Query("SELECT * FROM ProductItem ORDER BY name")
    List<ProductItem> loadProductList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProductList(List<ProductItem> path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProductItem(ProductItem path);


    @Query("SELECT * FROM CategoryItem ORDER BY name")
    List<CategoryItem> loadCategoryList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCategoryList(List<CategoryItem> path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCategoryItem(CategoryItem path);


    @Query("SELECT * FROM Cart WHERE isClosed = 0 ORDER BY _id DESC LIMIT 1")
    Cart loadLastCart();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveCart(Cart cart);

}