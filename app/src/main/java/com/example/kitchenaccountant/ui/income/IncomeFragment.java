package com.example.kitchenaccountant.ui.income;

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

        categorySpinner = root.findViewById(R.id.spinner_income_category);
        collectedFromEditText = root.findViewById(R.id.edit_text_collected_from);
        amountEditText = root.findViewById(R.id.edit_text_amount);
        remarksEditText = root.findViewById(R.id.edit_income_text_remarks);
        submitButton = root.findViewById(R.id.button_submit_income);
        receivedDateEditText = root.findViewById(R.id.edit_income_text_received_date);
        receivedDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(IncomeViewModel.class);

        submitButton.setOnClickListener(view -> {
            // Get data from input fields
            String category = categorySpinner.getSelectedItem().toString();
            String collectedFrom = collectedFromEditText.getText().toString();
            String amount = amountEditText.getText().toString();
            String receivedDate = receivedDateEditText.getText().toString();
            String remarks = remarksEditText.getText().toString();

            // Create JSON object
            JSONObject formDataJson = new JSONObject();
            try {
                formDataJson.put("category", category);
                formDataJson.put("collectedFrom", collectedFrom);
                formDataJson.put("amount", amount);
                formDataJson.put("receivedDate", receivedDate);
                formDataJson.put("remarks", remarks);
            } catch (JSONException e) {
                e.printStackTrace();
                return; // Return if JSON creation fails
            }

            // Convert JSON object to string
            String formDataString = formDataJson.toString();
            CloudFlareR2Operations.saveObjectIntoR2UsingCompletableFuture(formDataString);
            //Store JSON String in CloudFlare R2
            Toast.makeText(requireContext(), "Submission successful!", Toast.LENGTH_SHORT).show();
            // Reset input fields
            categorySpinner.setSelection(0); // Reset spinner selection to the first item
            collectedFromEditText.setText(""); // Clear collected from field
            amountEditText.setText(""); // Clear amount field
            receivedDateEditText.setText(""); // Clear received date field
            remarksEditText.setText(""); // Clear remarks field
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
                    // Update the received date EditText with the selected date
                    String dateString = String.format("%02d/%02d/%d", day, month1 + 1, year1);
                    receivedDateEditText.setText(dateString);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

}
