package com.kulikov.smart_fin.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.kulikov.smart_fin.MyRoundNumeric;
import com.kulikov.smart_fin.ProductList;
import com.kulikov.smart_fin.ProductMap;
import com.kulikov.smart_fin.db.CartItem;
import com.kulikov.smart_fin.db.ProductItem;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @PrimaryKey(autoGenerate = true)
    private long _id;
    private boolean isClosed;
    private long receiptTime;

    @Ignore
    private List<CartItem> items;

    @Ignore
    private static Cart instance;

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public Cart() {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public long getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(long receiptTime) {
        this.receiptTime = receiptTime;
    }

    public void setData(List<CartItem> items) {
        this.items = items;
    }

    public void setData(Cart cart) {
        this._id = cart._id;
        this.isClosed = cart.isClosed;
        this.receiptTime = cart.receiptTime;
        this.items.clear();
        this.items.addAll(cart.items);
    }

    private void append(int idx, CartItem item) {
        double new_value = item.getValue() + this.items.get(idx).getValue();
        item.setValue(new_value);

        if (ProductMap.getItems().containsKey(item.getUid_product())) {
            item.setSum(new_value * ProductMap.getItems().get(item.getUid_product()).getPrice());
        } else {
            item.setSum(0);
        }

        item.setReceipt_id(get_id());

        this.items.remove(idx);
        this.items.add(idx, item);
    }

    public void add(CartItem item) {
        int idx = this.items.indexOf(item);
        item.setReceipt_id(get_id());
        if (idx == -1) {
            this.items.add(item);
        } else {
            append(idx, item);
        }
    }

    public void add(ProductItem item, double value) {
        CartItem cartItem = new CartItem(item);
        cartItem.setReceipt_id(get_id());
        int idx = this.items.indexOf(cartItem);
        if (idx == -1) {
            cartItem.setValue(value);
            cartItem.setSum(item.getPrice()*value); // Округление внутри setSum
            this.items.add(cartItem);
        } else {
            append(idx, cartItem);
        }
    }

    public double calculateTotal() {
        double tot = 0.00;
        for (int i=0; i<items.size(); i++) {
            tot += items.get(i).getSum();
        }
        return tot;
    }

    public void clear() {
        this.items.clear();
        this.set_id(0);
        this.setClosed(false);
        this.setReceiptTime(0);
    }

    public List<CartItem> getData() {
        return this.items;
    }
}
