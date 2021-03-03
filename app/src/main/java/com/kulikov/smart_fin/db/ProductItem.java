package com.kulikov.smart_fin.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ProductItem {
    @PrimaryKey
    @NonNull
    private String uid;
    @NonNull
    private String uid_category;
    private String name;
    private double price;
    private int res_id_image;

    public ProductItem(String uid, String uid_category, String name, int res_id_image, double price) {
        this.uid = uid;
        this.uid_category = uid_category;
        this.name = name;
        this.res_id_image = res_id_image;
        this.price = price;
    }

    @Ignore
    public ProductItem() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid_category() {
        return uid_category;
    }

    public void setUid_category(String uid_category) {
        this.uid_category = uid_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRes_id_image() {
        return res_id_image;
    }

    public void setRes_id_image(int res_id_image) {
        this.res_id_image = res_id_image;
    }
}
