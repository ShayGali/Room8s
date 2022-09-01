package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.ExpensesAdapter;
import com.example.room8.model.Expense;

import java.util.function.Predicate;

public class ExpensesDialog extends AppCompatDialogFragment {
    private Predicate<Expense> filterMethod;

    public ExpensesDialog(Predicate<Expense> filterMethod) {
        this.filterMethod = filterMethod;
    }

    View view;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_recycler_view, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);

        initRecyclerView();


        return dialog;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ExpensesAdapter adapter = new ExpensesAdapter(getLayoutInflater(), filterMethod);
//        adapter.registerAdapterDataObserver(); TODO
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
