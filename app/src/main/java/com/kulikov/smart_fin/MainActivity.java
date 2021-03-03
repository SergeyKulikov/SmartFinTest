package com.kulikov.smart_fin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kulikov.smart_fin.R;
import com.kulikov.smart_fin.db.CategoryItem;
import com.kulikov.smart_fin.db.ProductItem;
import com.kulikov.smart_fin.db.SmatrfinDao;
import com.kulikov.smart_fin.ui.home.TabLayoutAdapter;


import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


/**
 1. Все данные хранятся в БД. Здесь только первичная генерация данных. Основной код в HomeFragments.
 2. На каждую вкладку по категория выводтся продукты (Fragment), которые к ней относятся
 3. Ввод количества запрашивается при нажатии на продукт. Реализовано через AlertDialog,
 хотя можно было и через startActivityForResult.
 4. Как только происходит добавление позиции в корзину (чек) идет запись в БД.
 5. Если закрыть программу и открыть снова, то данные из незакрытого чека подтянутся.
 6. Закрытие чека по кнопке "Оплатить".
 7. Обработку поворота экрана не делал, т.к. данные все равно сохранятся и востанавливаются.
 8. Связку таблиц по ключам не делал, т.к. в данном случае нет смысла.
 */



public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private SmatrfinDao smatrfinDaoDatbase;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        smatrfinDaoDatbase = SmatrfinApp.getInstance().getSmatrfinDatabase().getDaoDatabase();

        new ProductGenerator().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
               || super.onSupportNavigateUp();
    }


    private class ProductGenerator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (smatrfinDaoDatbase.loadProductList().size() > 0 ) {
                return null;
            }

            int[] img_res_id = {R.drawable.p_1, R.drawable.p_2, R.drawable.p_3, R.drawable.p_4, R.drawable.p_5, R.drawable.p_6, R.drawable.p_7};
            String[] product_names = {"Чеснок", "Укроп", "Свекла", "Петрушка", "Морковь", "Лук репчатый", "Картофель"};

            int[] category_color = {0xffffff33, 0xffff33ff};
            String[] category_names = {"Россия", "Зимбабве"};

            List<CategoryItem> categoryItems = new ArrayList<>();
            List<ProductItem> productItems = new ArrayList<>();

            for (int c = 0; c < category_color.length; c++) {
                CategoryItem item = new CategoryItem(
                        UUID.randomUUID().toString(),
                        category_names[c],
                        "КГ",
                        category_color[c]
                );

                categoryItems.add(item);
            }

            smatrfinDaoDatbase.saveCategoryList(categoryItems);

            for (int p = 0; p < product_names.length; p++) {
                ProductItem item = new ProductItem(
                        UUID.randomUUID().toString(),
                        (p < 4) ? categoryItems.get(0).getUid() : categoryItems.get(1).getUid(),
                        product_names[p],
                        img_res_id[p],
                        MyRoundNumeric.roundTo((Math.random() * 200))
                );

                productItems.add(item);
            }
            smatrfinDaoDatbase.saveProductList(productItems);

            return null;
        }
    }



}