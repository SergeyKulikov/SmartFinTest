package com.kulikov.smart_fin.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kulikov.smart_fin.MyRoundNumeric;
import com.kulikov.smart_fin.db.Cart;
import com.kulikov.smart_fin.ProductList;
import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.SmatrfinApp;
import com.kulikov.smart_fin.db.CartItem;
import com.kulikov.smart_fin.db.CategoryItem;
import com.kulikov.smart_fin.db.ProductItem;
import com.kulikov.smart_fin.db.SmatrfinDao;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    private final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel homeViewModel;
    private ViewPager2 myViewPager2;
    private Context context;
    private SmatrfinDao db;
    private List<CategoryItem> categoryList = new ArrayList<>();
    // private List<ProductItem> productList = new ArrayList<>();
    private ProductList productList;
    private TableLayout tableLayout;
    private TabLayout tabLayout;
    private TabLayoutAdapter tabLayoutAdapter;
    private Map<Integer, Boolean> done = new HashMap<>();

    private Cart cart;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerCart;

    private TextView tvClearCart, tvReceipt, tvTotal;
    private long currentReceiptId;
    private Button btnPay;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = SmatrfinApp.getInstance().getSmatrfinDatabase().getDaoDatabase();

        cart = new Cart();

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final FragmentActivity fragmentActivity = getActivity();

        context = getContext();

        tabLayoutAdapter = new TabLayoutAdapter(fragmentActivity, categoryList);

        myViewPager2 = root.findViewById(R.id.viewpager);
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(tabLayoutAdapter);

        tableLayout = root.findViewById(R.id.tableLayout);
        tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        new TabLayoutMediator(tabLayout, myViewPager2,
                (tab, position) -> tab.setText(categoryList.get(position).getName())
        ).attach();

        cartAdapter = new CartAdapter(getContext(), cart);
        recyclerCart = root.findViewById(R.id.rvCartItems);
        recyclerCart.setLayoutManager(new LinearLayoutManager(fragmentActivity));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fragmentActivity,
                RecyclerView.VERTICAL);
        recyclerCart.addItemDecoration(dividerItemDecoration);

        new Thread(new Runnable() {
            @Override
            public void run() {
                cartAdapter = new CartAdapter(fragmentActivity, cart);
                fragmentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerCart.setAdapter(cartAdapter);
                    }
                });
            }
        }).start();


        tvTotal = root.findViewById(R.id.tvTotal);
        btnPay = root.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CloseCart().execute();
            }
        });


        tvClearCart = root.findViewById(R.id.tvClearCart);
        tvClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // очистить базу данных от незакрытых позиций
                new ClearCart().execute();
            }
        });

        tvReceipt = root.findViewById(R.id.tvReceipt);

        new LoadTabs().execute();
        new LoadProducts().execute();
        new LoadLastReceipt().execute();

        myViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if (done.containsKey(position)) return;

                ProductFragment productFragment;

                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();

                List<ProductItem> tmp = new ArrayList<>();
                tmp.addAll(productList.getProductsByCategory(categoryList.get(position).getUid()));

                // добавляем фрагмент
                for (int i = 0; i < tmp.size(); i++) {
                    productFragment = new ProductFragment();
                    productFragment.setData(tmp.get(i), categoryList);

                    final ProductFragment currentFragment = productFragment;
                    currentFragment.clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getWeightFromVirtualKeyboard(
                                    currentFragment.getProductItem(), cart,
                                    cartAdapter
                            );
                        }
                    };

                    fragmentTransaction.add(R.id.fragment_container_view, productFragment);
                }

                fragmentTransaction.commit();
                done.put(position, true);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                cartAdapter.setData(cart);
                cartAdapter.notifyDataSetChanged();
                btnPay.setEnabled(cart.getItems().size()>0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        return root;
    }

    private class LoadTabs extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            categoryList = db.loadCategoryList();
            categoryList.add(0, new CategoryItem(null, "Все", "КГ", 0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tabLayoutAdapter.setData(categoryList);
            super.onPostExecute(aVoid);
        }
    }

    private class LoadProducts extends AsyncTask<Void, Void, Void> {
        public ProductFragment productFragment;

        @Override
        protected Void doInBackground(Void... voids) {
            if (productList == null) {
                productList = new ProductList(db.loadProductList());
            }
            return null;
        }
    }


    private class LoadLastReceipt extends AsyncTask<Void, Void, Cart> {
        @Override
        protected Cart doInBackground(Void... voids) {
            return db.loadLastCart();
        }

        @Override
        protected void onPostExecute(Cart lastCart) {
            super.onPostExecute(cart);

            if (lastCart != null) {
                cart.setData(lastCart);
                tvReceipt.setText(String.format("# %d", cart.get_id()));

                new LoadCartItems().execute(cart.get_id());
            } else {
                new SaveCart().execute();
            }

        }
    }

    private class LoadCartItems extends AsyncTask<Long, Void, List<CartItem>> {

        @Override
        protected List<CartItem> doInBackground(Long... longs) {
            return db.loadCartItems(longs[0]);
        }

        @Override
        protected void onPostExecute(List<CartItem> cartItems) {
            super.onPostExecute(cartItems);

            cart.setItems(cartItems);

            cartAdapter.setData(cart);
            cartAdapter.notifyDataSetChanged();
            btnPay.setEnabled(cart.getItems().size()>0);

            tvTotal.setText(String.format("%.2f", cart.calculateTotal()));
        }
    }


    private class SaveCart extends AsyncTask<Void, Void, Long> {

        @Override
        protected Long doInBackground(Void... voids) {
            return db.saveCart(cart);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if (aLong == 0) {
                Log.d(TAG, "Ошибка записи");
            } else {
                currentReceiptId = aLong;
                cart.set_id(currentReceiptId);
                tvReceipt.setText(String.format("# %d", cart.get_id()));
            }
        }
    }

    private class SaveCartItems extends AsyncTask<Void, Void, List<Long>> {

        @Override
        protected List<Long> doInBackground(Void... voids) {
            return db.saveCartItems(cart.getItems());
        }

        @Override
        protected void onPostExecute(List<Long> lst) {
            super.onPostExecute(lst);
            for (int i=0; i<lst.size(); i++) {
                cart.getItems().get(i).set_id(lst.get(i));
            }
            cartAdapter.setData(cart);
            cartAdapter.notifyDataSetChanged();
            btnPay.setEnabled(cart.getItems().size()>0);

            tvTotal.setText(String.format("%.2f", cart.calculateTotal()));
        }
    }

    private class CloseCart extends AsyncTask<Void, Void, Long> {

        @Override
        protected Long doInBackground(Void... voids) {
            cart.setClosed(true);
            cart.setReceiptTime(new Date().getTime());
            return db.saveCart(cart);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            cart.clear();

            new LoadLastReceipt().execute();

            cartAdapter.setData(cart);
            cartAdapter.notifyDataSetChanged();
            btnPay.setEnabled(cart.getItems().size()>0);

            tvTotal.setText(String.format("%.2f", cart.calculateTotal()));
        }
    }

    private class ClearCart extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.DeleteCartItems(cart.getItems());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cart.clear();
            cartAdapter.setData(cart);
            cartAdapter.notifyDataSetChanged();
            btnPay.setEnabled(cart.getItems().size()>0);
            tvTotal.setText(String.format("%.2f", cart.calculateTotal()));
        }
    }


    /** ------------------------------- */

    private String currentWeight;
    private double value;
    private boolean isSetDecimalSeparator = false;

    private double showCurrentWeight(TextView textView, String weight) {
        double value = Double.parseDouble(weight);
        double result = MyRoundNumeric.roundTo(value);

        textView.setText(String.valueOf(result) + " КГ");
        return result;
    }



    public CartItem getWeightFromVirtualKeyboard(ProductItem productItem, Cart cart, CartAdapter cartAdapter) {

        CartItem cartItem = new CartItem(productItem);

        AlertDialog dialog = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_keyboard2, null);

        TextView tvCalculation = view.findViewById(R.id.tvCalculation);

        currentWeight = "0";
        value = showCurrentWeight(tvCalculation, currentWeight);

        int[] button_res_id = {
                R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6,
                R.id.btnNum7, R.id.btnNum8, R.id.btnNum9,
                R.id.btnComma, R.id.btnNum0, R.id.btnClear
        };

        int idx = 0;
        for (int res_id : button_res_id) {
            Button button = view.findViewById(res_id);

            button.setTag(idx);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((Button)view).getText().toString();
                    int tag = (int) ((Button) view).getTag();
                    switch (tag) {
                        case 9:
                            setDecimal();
                            break;
                        case 11:
                            setZero();
                            break;
                        default:
                            addSimbol(((Button) view).getText().toString());
                    }

                    value = showCurrentWeight(tvCalculation, currentWeight);
                }

                private void addSimbol(String smb) {
                    if (currentWeight.isEmpty()) {
                        currentWeight = smb;
                        return;
                    }

                    if (currentWeight.substring(0, 1).equals("0") && !isSetDecimalSeparator) {
                        currentWeight = smb;
                    } else {
                        currentWeight = currentWeight + smb;
                    }
                }

                private void setZero() {
                    currentWeight = "0";
                    isSetDecimalSeparator = false;
                }

                private void setDecimal() {
                    DecimalFormatSymbols otherSymbols = otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
                    String separator = String.valueOf(otherSymbols.getDecimalSeparator());

                    if (!currentWeight.contains(separator)) {
                        currentWeight = currentWeight + separator;
                        isSetDecimalSeparator = true;
                    }
                }

            });

            idx++;
        }

        dialog.setView(view);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Закрываем окно
                cart.add(productItem, value);
                new SaveCartItems().execute();

                // cartAdapter.setData(cart);
                // cartAdapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отменить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Закрываем окно
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundColor(Color.argb(255, 25, 161, 216));
                positiveButton.setTextColor(Color.argb(255, 255, 255, 255));

                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.argb(255, 25, 161, 216));
            }
        });
        dialog.show();

        return cartItem;
    }

}