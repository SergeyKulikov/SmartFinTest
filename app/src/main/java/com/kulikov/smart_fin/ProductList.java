package com.kulikov.smart_fin;

import com.kulikov.smart_fin.db.ProductItem;

import java.util.ArrayList;
import java.util.List;

final public class ProductList {
    private static ProductList instance;
    private List<ProductItem> productItems;

    public static ProductList getInstance() {
        if (instance == null) {
            instance = new ProductList();
        }
        return instance;
    }

    private ProductList() {
    }

    public ProductList(List<ProductItem> productItems) {
        setData(productItems);
    }

    public void setData(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public List<ProductItem> getProductsByCategory (String category) {
        List<ProductItem> temp = new ArrayList<>();
        if (productItems == null) return temp;
        if (category == null) {
            temp.addAll(productItems);
        } else {
            for (int i=0; i< productItems.size(); i++) {
                if (productItems.get(i).getUid_category().equals(category)) {
                    temp.add(productItems.get(i));
                }
            }
        }
        return temp;
    }

}
