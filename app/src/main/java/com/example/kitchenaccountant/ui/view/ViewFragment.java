package com.example.kitchenaccountant.ui.view;

import static com.example.kitchenaccountant.utilities.Constants.EXPENSE;
import static com.example.kitchenaccountant.utilities.Constants.INCOME;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kitchenaccountant.R;
import com.example.kitchenaccountant.domain.ExpenseDomain;
import com.example.kitchenaccountant.domain.IncomeDomain;
import com.example.kitchenaccountant.ui.service.CloudFlareR2Operations;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewFragment extends Fragment {

    private Button viewIncomesButton;
    private Button viewExpensesButton;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;
    private LinearLayout containerLayout;
    /*private LinearLayout popupLayout;
    private Button closePopupButton;*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewIncomesButton = view.findViewById(R.id.button_view_incomes);
        viewExpensesButton = view.findViewById(R.id.button_view_expenses);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerYear = view.findViewById(R.id.spinner_year);
        /*popupLayout = view.findViewById(R.id.popup_layout);
        closePopupButton = view.findViewById(R.id.button_close_popup);*/
        containerLayout = view.findViewById(R.id.container_layout);
        setupMonthSpinner();
        setupYearSpinner();
        setupButtonListeners();

        //closePopupButton.setOnClickListener(v -> popupLayout.setVisibility(View.GONE));
    }

    private void setupButtonListeners() {
        viewIncomesButton.setOnClickListener(v -> fetchAndDisplayData(INCOME));
        viewExpensesButton.setOnClickListener(v -> fetchAndDisplayData(EXPENSE));
    }

    private void fetchAndDisplayData(String type) {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String fileName = type + selectedMonth + "_" + selectedYear;

        new Thread(() -> {
            List<Object> domains;
            if (INCOME.equals(type)) {
                List<IncomeDomain> incomes = CommonUtilities.getAllIncomes(CloudFlareR2Operations.getR2Object(fileName));
                if (null != incomes)
                    domains = new ArrayList<>(incomes);
                else {
                    domains = new ArrayList<>();
                }
            } else {
                List<ExpenseDomain> expenses = CommonUtilities.getAllExpenses(CloudFlareR2Operations.getR2Object(fileName));
                if (null != expenses)
                    domains = new ArrayList<>(expenses);
                else {
                    domains = new ArrayList<>();
                }
            }

            getActivity().runOnUiThread(() -> {
                containerLayout.removeAllViews();
                if (null == domains || domains.size() == 0) {
                    TextView noDataTextView = new TextView(getContext());
                    String typeModified = type.replaceAll("[^a-zA-Z]", " ");
                    String message = String.format("No %s Data Found For %s %s", typeModified.substring(0, 1).toUpperCase() + typeModified.substring(1), selectedMonth, selectedYear);
                    noDataTextView.setText(message);
                    containerLayout.addView(noDataTextView);
                }
                for (Object domain : domains) {
                    if (domain instanceof IncomeDomain) {
                        IncomeDomain incomeDomain = (IncomeDomain) domain;
                        View domainView = LayoutInflater.from(getContext()).inflate(R.layout.income_view_items, containerLayout, false);

                        TextView textCategory = domainView.findViewById(R.id.text_category);
                        TextView textCollectedFrom = domainView.findViewById(R.id.text_collected_from);
                        TextView textAmount = domainView.findViewById(R.id.text_amount);
                        TextView textReceivedDate = domainView.findViewById(R.id.text_received_date);
                        TextView textRemarks = domainView.findViewById(R.id.text_remarks);

                        textCategory.setText("Category: " + incomeDomain.category);
                        textCollectedFrom.setText("Collected From: " + incomeDomain.collectedFrom);
                        textAmount.setText("Amount: " + incomeDomain.amount);
                        textReceivedDate.setText("Received Date: " + incomeDomain.receivedDate);
                        textRemarks.setText("Remarks: " + incomeDomain.remarks);

                        containerLayout.addView(domainView);
                    } else if (domain instanceof ExpenseDomain) {
                        ExpenseDomain expenseDomain = (ExpenseDomain) domain;
                        View domainView = LayoutInflater.from(getContext()).inflate(R.layout.expense_view_items, containerLayout, false);
                        TextView textCategory = domainView.findViewById(R.id.text_category);
                        TextView textDescription = domainView.findViewById(R.id.text_description);
                        TextView textAmount = domainView.findViewById(R.id.text_amount);
                        TextView textExpenseDate = domainView.findViewById(R.id.text_expense_date);
                        TextView textPayVia = domainView.findViewById(R.id.text_pay_via);
                        TextView textRemittanceAddress = domainView.findViewById(R.id.text_remittance_address);
                        TextView textRemittanceMobile = domainView.findViewById(R.id.text_remittance_mobile);
                        TextView textRemittanceName = domainView.findViewById(R.id.text_remittance_name);

                        textCategory.setText("Category: " + expenseDomain.getCategory());
                        textDescription.setText("Description: " + expenseDomain.getDescription());
                        textAmount.setText("Amount: " + expenseDomain.getAmount());
                        textExpenseDate.setText("Expense Date: " + expenseDomain.getExpenseDate());
                        textPayVia.setText("Pay Via: " + expenseDomain.getPayVia());
                        textRemittanceAddress.setText("Remittance Address: " + expenseDomain.getRemittanceAddress());
                        textRemittanceMobile.setText("Remittance Mobile: " + expenseDomain.getRemittanceMobile());
                        textRemittanceName.setText("Remittance Name: " + expenseDomain.getRemittanceName());
                        containerLayout.addView(domainView);
                    }
                }

                //popupLayout.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void setupMonthSpinner() {
        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
    }

    private void setupYearSpinner() {
        List<Integer> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(i);
        }

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }
}

