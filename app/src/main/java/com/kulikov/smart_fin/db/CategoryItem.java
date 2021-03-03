package com.kulikov.smart_fin.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public
class CategoryItem {
    @PrimaryKey
    @NonNull
    private String uid;
    private String name;
    private String unit_name;
    private long color;

    public CategoryItem(String uid, String name, String unit_name, long color) {
        this.uid = uid;
        this.name = name;
        this.unit_name = unit_name;
        this.color = color;;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

}
