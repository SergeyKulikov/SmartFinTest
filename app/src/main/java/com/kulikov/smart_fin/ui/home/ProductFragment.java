package com.kulikov.smart_fin.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.db.CategoryItem;
import com.kulikov.smart_fin.db.ProductItem;

import java.util.List;

/**
 *
 */

public class ProductFragment extends Fragment {
    private ProductItem productItem;
    private long color;

    private TextView tvProductName, tvProductCalcutation;
    private ImageView ivGroupColor;
    private ImageView imageView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.product_item, container, false);
        view.setOnClickListener(clickListener);

        tvProductName = view.findViewById(R.id.tvProductName);
        imageView = view.findViewById(R.id.imageView);
        ivGroupColor = view.findViewById(R.id.ivGroupColor);

        imageView.setImageDrawable(getActivity().getResources().getDrawable(productItem.getRes_id_image()));
        tvProductName.setText(this.productItem.getName());

        // Это варант падает с ошибкой:
        // java.lang.IllegalArgumentException: Invalid ID, must be in the range [0..16]
        // ivGroupColor.setBackgroundColor(Color.toArgb(color));

        // А вот так все работает
        int A = (int)(color >> 24) & 0xff; // or color >>> 24
        int R = (int)(color >> 16) & 0xff;
        int G = (int)(color >>  8) & 0xff;
        int B = (int)(color      ) & 0xff;

        ivGroupColor.setBackgroundColor(Color.argb(A,R,G,B)); // from a color int);
        return view;
    }

    public View.OnClickListener clickListener = view -> {
        // Делаем public, чтобы можно было переопределить снаружи и там прописать, что делать
        // по нажалию на фрагмент
    };

    public void setData(ProductItem productItem, List<CategoryItem> categoryItems) {
        this.productItem = new ProductItem();
        this.productItem = productItem;

        // Цвет полосочки нужно забрать из категории
        for (int i = 0; i < categoryItems.size(); i++) {
            if (this.productItem.getUid_category().equals(categoryItems.get(i).getUid())) {
                color = categoryItems.get(i).getColor();
                break;
            }
        }
    }

    public ProductItem getProductItem () {
        return this.productItem;
    }

}
