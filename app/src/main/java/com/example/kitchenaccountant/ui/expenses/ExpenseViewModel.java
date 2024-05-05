package com.example.kitchenaccountant.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExpenseViewModel extends ViewModel {

    private MutableLiveData<String> formData;

    public ExpenseViewModel() {
        formData = new MutableLiveData<>();
    }

    public void updateFormData(String data) {
        formData.setValue(data);
    }

    public LiveData<String> getFormData() {
        return formData;
    }
}
