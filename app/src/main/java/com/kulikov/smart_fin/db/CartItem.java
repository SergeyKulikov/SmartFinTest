package com.kulikov.smart_fin.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.kulikov.smart_fin.MyRoundNumeric;

@Entity
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private long _id;

    private long receipt_id;
    @NonNull
    private String uid_product;
    private double value;
    private double sum;
    private String productName;

    public CartItem(ProductItem productItem) {
        uid_product = productItem.getUid();
        value = 0;
        sum = 0;
        productName = productItem.getName();
    }

    public CartItem() {
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUid_product() {
        return uid_product;
    }

    public void setUid_product(String uid_product) {
        this.uid_product = uid_product;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = MyRoundNumeric.roundTo(value);
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = MyRoundNumeric.roundTo(sum);
    }

    public long getReceipt_id() {
        return receipt_id;
    }

    public void setReceipt_id(long receipt_id) {
        this.receipt_id = receipt_id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
