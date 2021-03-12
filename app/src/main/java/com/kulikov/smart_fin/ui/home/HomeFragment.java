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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kulikov.smart_fin.MyRoundNumeric;
import com.kulikov.smart_fin.ProductList;
import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.SmatrfinApp;
import com.kulikov.smart_fin.db.Cart;
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

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;


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


    private final String separator = String.valueOf(new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator());
    private int decimal_count;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = SmatrfinApp.getInstance().getSmatrfinDatabase().getDaoDatabase();


        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        context = getContext();

        initUI(root);

        db.rx_loadCategoryList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryItems -> {
                    categoryItems.add(0, new CategoryItem(null, "Все", "КГ", 0));
                    categoryList = categoryItems;
                    tabLayoutAdapter.setData(categoryList);
                    // tabLayoutAdapter.notifyDataSetChanged();
                });


        db.rx_loadCategoryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryItems -> {
                    categoryItems.add(0, new CategoryItem(null, "Все", "КГ", 0));
                    categoryList = categoryItems;
                    tabLayoutAdapter.setData(categoryList);
                });

        productList = ProductList.getInstance();
        db.rx_loadProductList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productItems -> productList.setData(productItems));

        cart = Cart.getInstance();
        load_rxLastCart();

        return root;
    }

    private void load_rxLastCart() {
        db.rx_loadLastCart()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableMaybeObserver<Cart>() {
                    @Override
                    public void onSuccess(Cart cart1) {
                        cart.setData(cart1);

                        currentReceiptId = cart.get_id();
                        cart.set_id(currentReceiptId);
                        tvReceipt.setText(String.format("# %d", cart.get_id()));

                        load_rxCartItems();

                        tvReceipt.setText(String.format("# %d", cart.get_id()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        // ...
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        // Не удалось найте последнюю открытую запись
                        save_rxCart();
                    }
                });

    }

    private void load_rxCartItems() {
        db.rx_loadCartItems(cart.get_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    cart.setItems(cartItems);
                    cartAdapter.setData(cart);

                    btnPay.setEnabled(cart.getItems().size() > 0);
                    tvTotal.setText(String.format("%.2f", cart.calculateTotal()));
                });
    }

    private void initUI(View root) {
        final FragmentActivity fragmentActivity = getActivity();

        tvTotal = root.findViewById(R.id.tvTotal);
        btnPay = root.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.setClosed(true);
                cart.setReceiptTime(new Date().getTime());

                save_rxCart();
            }
        });


        tvClearCart = root.findViewById(R.id.tvClearCart);
        tvClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // очистить базу данных от незакрытых позиций
                // new ClearCart().execute();
                Clear_rxCart();
            }
        });

        tvReceipt = root.findViewById(R.id.tvReceipt);
        tableLayout = root.findViewById(R.id.tableLayout);
        tabLayout = root.findViewById(R.id.tab_layout);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        // созаем закладки и сдой данных
        tabLayoutAdapter = new TabLayoutAdapter(fragmentActivity, categoryList);
        myViewPager2 = root.findViewById(R.id.viewpager);
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(tabLayoutAdapter);
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
                btnPay.setEnabled(cart.getItems().size() > 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        new TabLayoutMediator(tabLayout, myViewPager2,
                (tab, position) -> tab.setText(categoryList.get(position).getName())
        ).attach();

        // Подлючаем корзину
        cartAdapter = new CartAdapter(getContext(), cart);
        recyclerCart = root.findViewById(R.id.rvCartItems);
        recyclerCart.setLayoutManager(new LinearLayoutManager(fragmentActivity));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fragmentActivity,
                RecyclerView.VERTICAL);
        recyclerCart.addItemDecoration(dividerItemDecoration);
        cartAdapter = new CartAdapter(fragmentActivity, cart);
        recyclerCart.setAdapter(cartAdapter);

    }

    private void save_rxCart() {
        db.rx_saveCart(cart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    cart.clear();
                    load_rxLastCart();
                });
    }

    private void save_rxCartItems() {
        db.rx_saveCartItems(cart.getItems())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        load_rxLastCart();

                        btnPay.setEnabled(cart.getItems().size()>0);
                        tvTotal.setText(String.format(Locale.getDefault(), "%.2f", cart.calculateTotal()));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });
    }

    private void Clear_rxCart() {
        db.rx_DeleteCartItems(cart.getItems())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) throws Exception {
                        cart.clear();
                        load_rxLastCart();
                    }
                });
    }


    /**
     * -------------------------------
     */

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

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_keyboard2, null);

        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvProductCalculation = view.findViewById(R.id.tvProductCalculation1);
        TextView tvCalculation = view.findViewById(R.id.tvCalculation);

        currentWeight = "0";
        value = showCurrentWeight(tvCalculation, currentWeight);
        decimal_count = 0;

        tvProductName.setText(productItem.getName());
        tvProductCalculation.setText(String.format("%.2f",MyRoundNumeric.roundTo(productItem.getPrice()*value)));

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

                    tvProductCalculation.setText(String.format("%.2f",MyRoundNumeric.roundTo(productItem.getPrice()*value)));
                }

                private void addSimbol(String smb) {
                    if (currentWeight.isEmpty()) {
                        currentWeight = smb;
                        return;
                    }

                    if (currentWeight.startsWith("0") && !isSetDecimalSeparator) {
                        currentWeight = smb;
                    } else {
                        if (decimal_count++ < 2) {
                            currentWeight = currentWeight + smb;
                        }
                    }
                }

                private void setZero() {
                    currentWeight = "0";
                    isSetDecimalSeparator = false;
                }

                private void setDecimal() {

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
                save_rxCartItems();
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

        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setBackgroundColor(Color.argb(255, 25, 161, 216));
            positiveButton.setTextColor(Color.argb(255, 255, 255, 255));

            Button negativeButton = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(Color.argb(255, 25, 161, 216));
        });
        dialog.show();

        return cartItem;
    }

}