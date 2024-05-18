package com.example.kitchenaccountant.ui.expenses;

import static com.example.kitchenaccountant.utilities.Constants.EXPENSE;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kitchenaccountant.R;
import com.example.kitchenaccountant.ui.service.CloudFlareR2Operations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ExpenseFragment extends Fragment {

    private Spinner categorySpinner;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private EditText expenseDateEditText;
    private EditText remittanceNameEditText;
    private EditText remittanceMobileEditText;
    private EditText remittanceAddressEditText;
    private Spinner payViaSpinner;
    private Button submitButton;
    private ExpenseViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expenses, container, false);

        categorySpinner = root.findViewById(R.id.spinner_category);
        descriptionEditText = root.findViewById(R.id.edit_text_description);
        amountEditText = root.findViewById(R.id.edit_text_amount);
        expenseDateEditText = root.findViewById(R.id.edit_text_expense_date);
        remittanceNameEditText = root.findViewById(R.id.edit_text_remittance_name);
        remittanceMobileEditText = root.findViewById(R.id.edit_text_remittance_mobile);
        remittanceAddressEditText = root.findViewById(R.id.edit_text_remittance_address);
        payViaSpinner = root.findViewById(R.id.spinner_pay_via);
        submitButton = root.findViewById(R.id.button_submit_expense);

        expenseDateEditText.setOnClickListener(view -> showDatePickerDialog());

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> payViaAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.pay_via_options, android.R.layout.simple_spinner_item);
        payViaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payViaSpinner.setAdapter(payViaAdapter);

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        submitButton.setOnClickListener(view -> {
            String category = categorySpinner.getSelectedItem().toString();
            String description = descriptionEditText.getText().toString();
            String amount = amountEditText.getText().toString();
            String expenseDate = expenseDateEditText.getText().toString();
            String remittanceName = remittanceNameEditText.getText().toString();
            String remittanceMobile = remittanceMobileEditText.getText().toString();
            String remittanceAddress = remittanceAddressEditText.getText().toString();
            String payVia = payViaSpinner.getSelectedItem().toString();

            JSONObject formDataJson = new JSONObject();
            try {
                formDataJson.put("category", category);
                formDataJson.put("description", description);
                formDataJson.put("amount", amount);
                formDataJson.put("expenseDate", expenseDate);
                formDataJson.put("remittanceName", remittanceName);
                formDataJson.put("remittanceMobile", remittanceMobile);
                formDataJson.put("remittanceAddress", remittanceAddress);
                formDataJson.put("payVia", payVia);
            } catch (JSONException e) {
                e.printStackTrace();
                return; // Return if JSON creation fails
            }

            String formDataString = formDataJson.toString();
            CloudFlareR2Operations.saveObjectIntoR2UsingCompletableFuture(EXPENSE, formDataString);
            //Store JSON String in CloudFlare R2
            Toast.makeText(requireContext(), "Submission successful!", Toast.LENGTH_SHORT).show();
            // Reset input fields
            categorySpinner.setSelection(0);
            descriptionEditText.setText("");
            amountEditText.setText("");
            expenseDateEditText.setText("");
            remittanceNameEditText.setText("");
            remittanceMobileEditText.setText("");
            remittanceAddressEditText.setText("");
            payViaSpinner.setSelection(0);
        });

        return root;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (datePicker, year1, month1, day) -> {
                    String dateString = String.format("%02d/%02d/%d", day, month1 + 1, year1);
                    expenseDateEditText.setText(dateString);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}


