package com.example.kitchenaccountant.ui.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kitchenaccountant.R;
import com.example.kitchenaccountant.domain.IncomeDomain;
import com.example.kitchenaccountant.ui.service.CloudFlareR2Operations;
import com.example.kitchenaccountant.utilities.CommonUtilities;

import java.util.List;

public class ViewFragment extends Fragment {

    private Button viewIncomesButton;
    private Button viewExpensesButton;
    private TextView descriptionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        // Bind views
        viewIncomesButton = view.findViewById(R.id.button_view_incomes);
        viewExpensesButton = view.findViewById(R.id.button_view_expenses);
        descriptionTextView = view.findViewById(R.id.text_view_description);
        LinearLayout containerLayout = view.findViewById(R.id.container_layout);

        // Set click listeners
        viewIncomesButton.setOnClickListener(v -> {
            // Show loading message
            updateDescription("Loading...");

            // Fetch data asynchronously
            new Thread(() -> {
                List<IncomeDomain> incomeDomains = CommonUtilities.getAllIncomes(CloudFlareR2Operations.getR2Object());

                // Update UI on the main thread
                getActivity().runOnUiThread(() -> {
                    // Clear existing views
                    containerLayout.removeAllViews();

                    // Populate container layout with new views
                    for (IncomeDomain incomeDomain : incomeDomains) {
                        // Inflate the income_domain_item.xml layout
                        View incomeDomainView = LayoutInflater.from(getContext()).inflate(R.layout.income_view_items, containerLayout, false);

                        // Find views in the inflated layout
                        TextView textCategory = incomeDomainView.findViewById(R.id.text_category);
                        TextView textCollectedFrom = incomeDomainView.findViewById(R.id.text_collected_from);
                        TextView textAmount = incomeDomainView.findViewById(R.id.text_amount);
                        TextView textReceivedDate = incomeDomainView.findViewById(R.id.text_received_date);
                        TextView textRemarks = incomeDomainView.findViewById(R.id.text_remarks);

                        // Set data to views
                        textCategory.setText("Category: " + incomeDomain.category);
                        textCollectedFrom.setText("Collected From: " + incomeDomain.collectedFrom);
                        textAmount.setText("Amount: " + incomeDomain.amount);
                        textReceivedDate.setText("Received Date: " + incomeDomain.receivedDate);
                        textRemarks.setText("Remarks: " + incomeDomain.remarks);

                        // Add the inflated view to the container layout
                        containerLayout.addView(incomeDomainView);
                    }

                    // Hide loading message after data is fetched
                    updateDescription("");
                });
            }).start();
        });

        viewExpensesButton.setOnClickListener(v -> {
            containerLayout.removeAllViews();
            updateDescription("Expense list");
        });

// Initially show loading message
        updateDescription("Loading...");

        return view;

    }

    private void updateDescription(final String description) {
        new Handler(Looper.getMainLooper()).post(() -> {
            descriptionTextView.setText(description);
        });
    }
}

