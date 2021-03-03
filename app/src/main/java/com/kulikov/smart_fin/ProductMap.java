package com.kulikov.smart_fin;


import com.kulikov.smart_fin.db.ProductItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProductMap {
    private static Map<String, ProductItem> items;

    public ProductMap(List<ProductItem> itemList) {
        for (int i=0; i<itemList.size(); i++) {
            items.put(itemList.get(i).getUid(), itemList.get(i));
        }
    }

    public static List<ProductItem> getAsArrayList() {
        Collection<ProductItem> values = items.values();
        return new ArrayList<ProductItem>(values);
    }

    public static Map<String, ProductItem> getItems() {
        return items;
    }
}
