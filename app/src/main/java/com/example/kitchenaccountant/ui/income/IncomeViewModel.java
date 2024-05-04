package com.example.kitchenaccountant.ui.income;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IncomeViewModel extends ViewModel {

    /*private final MutableLiveData<String> mText;

    public IncomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is income fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }*/
    private MutableLiveData<String> formData;

    public IncomeViewModel() {
        formData = new MutableLiveData<>();
    }

    // Method to update form data
    public void updateFormData(String data) {
        formData.setValue(data);
    }

    // Method to retrieve form data
    public LiveData<String> getFormData() {
        return formData;
    }
}