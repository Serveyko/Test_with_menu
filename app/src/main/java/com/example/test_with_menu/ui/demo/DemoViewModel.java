package com.example.test_with_menu.ui.demo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DemoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DemoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is demo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}