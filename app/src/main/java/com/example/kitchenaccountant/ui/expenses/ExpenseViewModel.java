package com.example.kitchenaccountant.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpenseViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExpenseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is expenses fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}