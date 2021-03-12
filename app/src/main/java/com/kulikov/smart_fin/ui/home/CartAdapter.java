package com.kulikov.smart_fin.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kulikov.smart_fin.db.Cart;
import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.db.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, Cart cart) {
        this.context = context;
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>();
        }
        if (cart != null) {
            this.cartItems.clear();
            this.cartItems.addAll(cart.getItems());

        }
        notifyDataSetChanged();
    }

    public void setData(Cart cart) {
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>();
        }

        this.cartItems.clear();
        this.cartItems.addAll(cart.getData());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvProductName.setText(cartItems.get(position).getProductName());
        holder.tvProductCalculation.setText(getCalculationString(position));
        holder.getTvProductPrice.setText(getPriceString(position));
        holder.tvProductSum.setText( String.format(Locale.getDefault(), "%.2f", cartItems.get(position).getSum()) );
    }

    @SuppressLint("DefaultLocale")
    private String getCalculationString(int position) {
        return String.format("1 КГ х %.2f",
                cartItems.get(position).getSum()/cartItems.get(position).getValue()
        );
    }

    @SuppressLint("DefaultLocale")
    private String getPriceString(int position) {
        return String.format("%.2f КГ х %.2f", cartItems.get(position).getValue(),
                cartItems.get(position).getSum()/cartItems.get(position).getValue()
        );
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName, tvProductCalculation, getTvProductPrice, tvProductSum;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCalculation = itemView.findViewById(R.id.tvProductCalcutation);
            getTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductSum = itemView.findViewById(R.id.tvProductSum);
        }
    }
}