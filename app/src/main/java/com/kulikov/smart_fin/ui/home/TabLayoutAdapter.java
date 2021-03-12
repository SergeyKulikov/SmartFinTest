package com.kulikov.smart_fin.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.db.CategoryItem;
import com.kulikov.smart_fin.db.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Адпатер категорий товаровы
 *
 */


public class TabLayoutAdapter extends RecyclerView.Adapter<TabLayoutAdapter.MyViewHolder> {

    private Context context;
    private List<CategoryItem> arrayList;

    public TabLayoutAdapter(Context context, List<CategoryItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public void setData (List<CategoryItem> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // в R.layout.list_item - храним/создаем сетку товаров, но не как таблицу, а
        // виде фрагментов. осути это пустой layout.
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Слои первоначально пустые и там нет элементов, поэтому здесь ничего не делаем
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}