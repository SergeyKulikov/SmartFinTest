package com.kulikov.smart_fin.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kulikov.smart_fin.SmatrfinApp;
import com.kulikov.smart_fin.db.CategoryItem;
import com.kulikov.smart_fin.db.SmatrfinDao;
import com.kulikov.smart_fin.db.SmatrfinDaoDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    // private MutableLiveData<List<CategoryItem>> mCategories;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        // mCategories = new MutableLiveData<>();
        // mCategories.postValue(db.loadCategoryList());
    }

    public LiveData<String> getText() {
        return mText;
    }
    /*public LiveData<List<CategoryItem>> getCategories() {
        return mCategories;
    }*/


}