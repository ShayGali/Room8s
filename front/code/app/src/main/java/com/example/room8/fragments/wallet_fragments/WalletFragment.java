package com.example.room8.fragments.wallet_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.dialogs.ExpensesDialog;
import com.example.room8.model.User;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WalletFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        fetchAllExpenses();

        view.findViewById(R.id.create_expense_btn).setOnClickListener(v -> {
        });

        view.findViewById(R.id.previous_expenses_btn).setOnClickListener(v -> new ExpensesDialog(null, "previous expenses").show(getParentFragmentManager(), "previous_expenses"));
        view.findViewById(R.id.my_expenses_btn).setOnClickListener(v -> new ExpensesDialog(expense -> expense.getUserId() == User.getInstance().getId(), "my expenses").show(getParentFragmentManager(), "my_expenses"));

        view.findViewById(R.id.monthly_expenses_btn).setOnClickListener((v) -> new ExpensesDialog(expense -> {
            if (expense.getPaymentDate() == null) return false;

            Calendar today = Calendar.getInstance(TimeZone.getDefault());
            Calendar expenseDate = Calendar.getInstance(TimeZone.getDefault());

            today.setTime(new Date());
            expenseDate.setTime(expense.getPaymentDate());
            return today.get(Calendar.YEAR) == expenseDate.get(Calendar.YEAR) && today.get(Calendar.MONTH) == expenseDate.get(Calendar.MONTH);

        }, "monthly expenses").show(getParentFragmentManager(), "monthly_expenses"));
        return view;
    }


    private void fetchAllExpenses() {
        ServerRequestsService.getInstance().getExpenses();
    }
}