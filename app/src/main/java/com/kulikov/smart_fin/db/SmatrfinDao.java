package com.kulikov.smart_fin.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface SmatrfinDao {
    /** Исключительно для создания продукта при первом запуске в MainActivity */

    @Query("SELECT * FROM ProductItem ORDER BY name")
    List<ProductItem> loadProductList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProductList(List<ProductItem> path);

    @Query("SELECT * FROM CategoryItem ORDER BY name")
    List<CategoryItem> loadCategoryList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCategoryList(List<CategoryItem> path);

    /** RxJava2 */

    @Query("SELECT * FROM CategoryItem ORDER BY name")
    Flowable<List<CategoryItem>> rx_loadCategoryList();

    @Query("SELECT * FROM ProductItem ORDER BY name")
    Flowable<List<ProductItem>> rx_loadProductList();

    @Query("SELECT * FROM Cart WHERE isClosed = 0 ORDER BY _id DESC LIMIT 1")
    Maybe<Cart> rx_loadLastCart();

    @Query("SELECT * FROM CartItem WHERE receipt_id = :receipt_id")
    Flowable<List<CartItem>> rx_loadCartItems(Long receipt_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> rx_saveCart(Cart cart);

    @Delete()
    Maybe<Void> rx_DeleteCartItems(List<CartItem> path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable rx_saveCartItems(List<CartItem> items);

}