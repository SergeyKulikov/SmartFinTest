package com.kulikov.smart_fin;

import com.kulikov.smart_fin.db.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private List<ProductItem> productItems;

    public ProductList(List<ProductItem> productItems) {
        this.productItems = productItems;
    }

    public List<ProductItem> getProductsByCategory (String category) {
        List<ProductItem> temp = new ArrayList<>();
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
