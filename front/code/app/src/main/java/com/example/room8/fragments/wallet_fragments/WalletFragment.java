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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        fetchAllExpenses();

        view.findViewById(R.id.create_expense_btn).setOnClickListener(v -> {
        });

        view.findViewById(R.id.previous_expenses_btn).setOnClickListener(v -> new ExpensesDialog(null, "previous expenses").show(getParentFragmentManager(),"previous_expenses"));
        view.findViewById(R.id.my_expenses_btn).setOnClickListener(v -> new ExpensesDialog(expense -> expense.getUserId() == User.getInstance().getId(), "my expenses").show(getParentFragmentManager(),"my_expenses"));

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