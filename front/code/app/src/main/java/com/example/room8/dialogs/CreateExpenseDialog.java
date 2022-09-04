package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.model.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

public class CreateExpenseDialog extends AppCompatDialogFragment {

    Expense tempExpense;

    View view;

    TextInputLayout titleInputLayout;
    TextInputEditText titleInputEditText;
    Spinner typeInput;
    FloatingActionButton dateInput;
    EditText amountInput;
    EditText noteInput;

    public CreateExpenseDialog() {
        this.tempExpense = new Expense();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_create_expense, null);
        initInputFields();

        builder.setView(view);

        view.findViewById(R.id.close_dialog_btn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.ok).setOnClickListener(v -> saveExpense());


        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);

        return dialog;
    }

    private void saveExpense() {
        getDataFromInputs();
        if ("".equals(tempExpense.getTitle())) {
            titleInputLayout.setError("fill");
        } else {
            ServerRequestsService.getInstance().createExpense(tempExpense);

            dismiss();
        }
    }

    private void getDataFromInputs() {
        this.tempExpense.setTitle(Objects.requireNonNull(titleInputEditText.getText()).toString().trim());
        this.tempExpense.setType(typeInput.getSelectedItem().toString());
        if (!"".equals(amountInput.getText().toString().trim()))
            this.tempExpense.setAmount(Double.parseDouble(amountInput.getText().toString()));
        this.tempExpense.setNote(noteInput.getText().toString());
    }

    private void initInputFields() {
        this.titleInputLayout = view.findViewById(R.id.expense_title_input_layout);
        this.titleInputEditText = view.findViewById(R.id.expense_title_input_EditText);
        this.typeInput = view.findViewById(R.id.expense_type_input);
        this.dateInput = view.findViewById(R.id.expense_date_input);
        this.amountInput = view.findViewById(R.id.expense_amount_input);
        this.noteInput = view.findViewById(R.id.expense_note_input);

        initSpinner();
        initDatePicker();
    }

    private void initSpinner() {
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), R.layout.view_drop_down_item, Expense.EXPENSE_TYPES);
        typeInput.setAdapter(typesAdapter);
    }

    private void initDatePicker() {
        TextView dateTextView = view.findViewById(R.id.expense_date_text);

        this.dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (tempExpense.getPaymentDate() != null)
                calendar.setTime(tempExpense.getPaymentDate());

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tempExpense.setPaymentDate(calendar.getTime());
                dateTextView.setText(ServerRequestsService.DATE_FORMAT.format(calendar.getTime()));
            };
            new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}
