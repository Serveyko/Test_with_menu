package com.example.test_with_menu.ui.plus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlusViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlusViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}