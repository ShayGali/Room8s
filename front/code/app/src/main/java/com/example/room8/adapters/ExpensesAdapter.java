package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;
import com.example.room8.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExpensesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private final ArrayList<Expense> expenses;
    private final Runnable openKeyboardFunction;
    private final Predicate<Expense> filterMethod;

    public ExpensesAdapter(LayoutInflater inflater, Predicate<Expense> filterMethod, Runnable openKeyboardFunction) {
        this.inflater = inflater;
        if (filterMethod != null)
            this.expenses = (ArrayList<Expense>) Apartment.getInstance().getExpenses().stream().filter(filterMethod).collect(Collectors.toList());
        else
            this.expenses = Apartment.getInstance().getExpenses();

        this.openKeyboardFunction = openKeyboardFunction;
        this.filterMethod = filterMethod;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseHolder(inflater.inflate(R.layout.view_single_expense_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        ExpenseHolder expenseHolder = (ExpenseHolder) holder;

        expenseHolder.title.setText(expense.getTitle());
        expenseHolder.creatorName.setText(Apartment.getInstance().getRoom8NameById(expense.getUserId()));
        expenseHolder.amount.setText(String.valueOf(expense.getAmount()));
        if (expense.getUploadDate() != null)
            expenseHolder.uploadDate.setText(ServerRequestsService.DATE_FORMAT.format(expense.getUploadDate()));
        if (expense.getPaymentDate() != null)
            expenseHolder.paymentDate.setText(ServerRequestsService.DATE_FORMAT.format(expense.getPaymentDate()));
        expenseHolder.note.setText(expense.getNote());

        expenseHolder.type.setEnabled(false);
        expenseHolder.type.setClickable(false);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(inflater.getContext(), R.layout.view_drop_down_item, Expense.EXPENSE_TYPES);
        expenseHolder.type.setAdapter(typesAdapter);
        for (int i = 0; i < Expense.EXPENSE_TYPES.length; i++) {
            if (Expense.EXPENSE_TYPES[i].equals(expense.getType())) {
                expenseHolder.type.setSelection(i);
                break;
            }
        }

        expenseHolder.enterEditModeBtn.setOnClickListener(v -> {
            enterEditMode(expenseHolder, expense, position);
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    @SuppressLint("ResourceAsColor")
    void enterEditMode(@NonNull ExpenseHolder holder, Expense expense, int position) {
        Expense temp = new Expense(expense);
        holder.enterEditModeBtn.setVisibility(View.INVISIBLE);

        holder.editTitleBtn.setVisibility(View.VISIBLE);
        holder.editTypeBtn.setVisibility(View.VISIBLE);
        holder.editAmountBtn.setVisibility(View.VISIBLE);
        holder.editPaymentDateBtn.setVisibility(View.VISIBLE);
        holder.editNoteBtn.setVisibility(View.VISIBLE);
        holder.saveChangesBtn.setVisibility(View.VISIBLE);
        holder.dismissChangesBtn.setVisibility(View.VISIBLE);

        setOnClick(holder.title, holder.editTitleBtn, holder.temp.getBackground());
        setOnClick(holder.amount, holder.editAmountBtn, holder.temp.getBackground());
        setOnClick(holder.note, holder.editNoteBtn, holder.temp.getBackground());

        holder.editPaymentDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (temp.getPaymentDate() != null)
                calendar.setTime(temp.getPaymentDate());
            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                temp.setPaymentDate(calendar.getTime());
                holder.paymentDate.setText(ServerRequestsService.DATE_FORMAT.format(temp.getPaymentDate()));
            };
            new DatePickerDialog(inflater.getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        AtomicBoolean isTypeClicked = new AtomicBoolean(false);
        holder.editTypeBtn.setOnClickListener(v -> {
            if (!isTypeClicked.get()) {
                isTypeClicked.set(true);
                holder.editTypeBtn.setImageResource(R.drawable.ic_baseline_close_24);
                holder.type.setEnabled(true);
                holder.type.setClickable(true);
                holder.type.performClick();
            } else {
                isTypeClicked.set(false);
                holder.editTypeBtn.setImageResource(R.drawable.ic_baseline_edit_24);
                holder.type.setEnabled(false);
                holder.type.setClickable(false);
            }
        });

        holder.saveChangesBtn.setOnClickListener(v -> exitEditMode(holder, expense, temp, true, position));
        holder.dismissChangesBtn.setOnClickListener(v -> exitEditMode(holder, expense, null, false, position));
    }

    @SuppressLint("ResourceAsColor")
    void setOnClick(EditText editText, FloatingActionButton fab, Drawable defaultColor) {
        AtomicBoolean isClicked = new AtomicBoolean(false);
        fab.setOnClickListener(v -> {
            if (!isClicked.get()) {
                isClicked.set(true);
                fab.setImageResource(R.drawable.ic_baseline_close_24);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.setClickable(true);
                editText.setBackground(defaultColor);
                openKeyboardFunction.run();
                editText.requestFocus();
            } else {
                isClicked.set(false);
                editText.setBackgroundColor(Color.TRANSPARENT);
                fab.setImageResource(R.drawable.ic_baseline_edit_24);
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setClickable(false);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void exitEditMode(@NonNull ExpenseHolder holder, Expense originalExpense, Expense tempExpense, boolean shouldUpdate, int position) {

        if (shouldUpdate) {
            tempExpense.setType(holder.type.getSelectedItem().toString());
            originalExpense.update(tempExpense);
            if (this.filterMethod != null && !this.filterMethod.test(originalExpense)) {
                this.expenses.removeIf(expense -> originalExpense.getId() == expense.getId());
            }
        } else {
            holder.amount.setText(String.valueOf(originalExpense.getAmount()));
            for (int i = 0; i < Expense.EXPENSE_TYPES.length; i++) {
                if (Expense.EXPENSE_TYPES[i].equals(originalExpense.getType())) {
                    holder.type.setSelection(i);
                    break;
                }
            }
            holder.paymentDate.setText(ServerRequestsService.DATE_FORMAT.format(originalExpense.getPaymentDate()));
            holder.note.setText(originalExpense.getNote());
        }
        notifyItemChanged(position);

        holder.enterEditModeBtn.setVisibility(View.VISIBLE);
        holder.editTitleBtn.setVisibility(View.INVISIBLE);
        holder.editTypeBtn.setVisibility(View.INVISIBLE);
        holder.editAmountBtn.setVisibility(View.INVISIBLE);
        holder.editPaymentDateBtn.setVisibility(View.INVISIBLE);
        holder.editNoteBtn.setVisibility(View.INVISIBLE);
        holder.saveChangesBtn.setVisibility(View.INVISIBLE);
        holder.dismissChangesBtn.setVisibility(View.INVISIBLE);

        holder.title.setBackgroundColor(Color.TRANSPARENT);
        holder.editTitleBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        holder.amount.setBackgroundColor(Color.TRANSPARENT);
        holder.editAmountBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        holder.note.setBackgroundColor(Color.TRANSPARENT);
        holder.editNoteBtn.setImageResource(R.drawable.ic_baseline_edit_24);

    }

    private static class ExpenseHolder extends RecyclerView.ViewHolder {
        EditText title;
        TextView creatorName;
        Spinner type;
        EditText amount;
        TextView uploadDate;
        TextView paymentDate;
        EditText note;
        FloatingActionButton enterEditModeBtn;

        FloatingActionButton editTitleBtn;
        FloatingActionButton editTypeBtn;
        FloatingActionButton editAmountBtn;
        FloatingActionButton editPaymentDateBtn;
        FloatingActionButton editNoteBtn;
        FloatingActionButton saveChangesBtn;
        FloatingActionButton dismissChangesBtn;

        EditText temp;

        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expense_title);
            creatorName = itemView.findViewById(R.id.expense_creator);
            type = itemView.findViewById(R.id.expense_type);
            amount = itemView.findViewById(R.id.expense_amount);
            uploadDate = itemView.findViewById(R.id.expense_uplaod_date);
            paymentDate = itemView.findViewById(R.id.expense_payment_date);
            note = itemView.findViewById(R.id.expense_note);
            enterEditModeBtn = itemView.findViewById(R.id.edit_expense_btn);

            editTitleBtn = itemView.findViewById(R.id.edit_title_btn);
            editTypeBtn = itemView.findViewById(R.id.edit_type_btn);
            editAmountBtn = itemView.findViewById(R.id.edit_amount_btn);
            editPaymentDateBtn = itemView.findViewById(R.id.edit_payment_btn);
            editNoteBtn = itemView.findViewById(R.id.edit_note_btn);
            saveChangesBtn = itemView.findViewById(R.id.save_changes_btn);
            dismissChangesBtn = itemView.findViewById(R.id.dismiss_changes_btn);

            temp = itemView.findViewById(R.id.temp);

        }


    }
}
