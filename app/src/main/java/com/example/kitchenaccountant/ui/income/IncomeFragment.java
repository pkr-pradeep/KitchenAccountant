package com.example.kitchenaccountant.ui.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kitchenaccountant.R;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {

    private Spinner categorySpinner;
    private EditText collectedFromEditText;
    private EditText amountEditText;
    private EditText receivedDateEditText;
    private EditText remarksEditText;
    private Button submitButton;
    private IncomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_incomes, container, false);

        categorySpinner = root.findViewById(R.id.spinner_category);
        collectedFromEditText = root.findViewById(R.id.edit_text_collected_from);
        amountEditText = root.findViewById(R.id.edit_text_amount);
        receivedDateEditText = root.findViewById(R.id.edit_text_received_date);
        remarksEditText = root.findViewById(R.id.edit_text_remarks);
        submitButton = root.findViewById(R.id.button_submit_income);

        /*// Populate income category dropdown
        List<String> categories = new ArrayList<>();
        categories.add("Category 1");
        categories.add("Category 2");*/

        // Add more categories as needed
        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(IncomeViewModel.class);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get data from input fields
                String category = categorySpinner.getSelectedItem().toString();
                String collectedFrom = collectedFromEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String receivedDate = receivedDateEditText.getText().toString();
                String remarks = remarksEditText.getText().toString();

                // Concatenate all data into a single string or use a custom data class
                String formData = "Category: " + category + "\n" +
                        "Collected From: " + collectedFrom + "\n" +
                        "Amount: " + amount + "\n" +
                        "Received Date: " + receivedDate + "\n" +
                        "Remarks: " + remarks;

                // Update ViewModel with form data
                viewModel.updateFormData(formData);
            }
        });

        return root;
    }
}
