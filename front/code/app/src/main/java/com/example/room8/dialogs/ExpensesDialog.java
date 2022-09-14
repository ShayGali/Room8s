package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.ExpensesAdapter;
import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;

import java.util.function.Predicate;

public class ExpensesDialog extends AppCompatDialogFragment {
    private final Predicate<Expense> filterMethod;
    private final String title;

    public ExpensesDialog(Predicate<Expense> filterMethod, String title) {
        this.filterMethod = filterMethod;
        this.title = title;
    }

    View view;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_recycler_view, null);

        ((TextView) view.findViewById(R.id.recycler_title)).setText(title);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        initRecyclerView();
        view.findViewById(R.id.close_dialog_btn).setOnClickListener(v -> System.out.println(Apartment.getInstance().getExpenses()));


        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // setting width to 90% of display
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);

        // setting height to 90% of display
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.9f);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ExpensesAdapter adapter = new ExpensesAdapter(requireActivity(), getLayoutInflater(), filterMethod, () -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
