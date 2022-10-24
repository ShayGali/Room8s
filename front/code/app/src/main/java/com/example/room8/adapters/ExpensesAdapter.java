package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExpensesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity activity;
    private final LayoutInflater inflater;
    private final ArrayList<Expense> expenses;
    private final Runnable openKeyboardFunction;
    private final Predicate<Expense> filterMethod;

    public ExpensesAdapter(Activity activity, LayoutInflater inflater, Predicate<Expense> filterMethod, Runnable openKeyboardFunction) {
        this.activity = activity;
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

        expenseHolder.titleEditText.setText(expense.getTitle());
        expenseHolder.creatorNameTextView.setText(Apartment.getInstance().getRoom8NameById(expense.getUserId()));
        expenseHolder.amountEditText.setText(String.valueOf(expense.getAmount()));
        if (expense.getUploadDate() != null)
            expenseHolder.uploadDateTextView.setText(ServerRequestsService.DATE_FORMAT.format(expense.getUploadDate()));
        if (expense.getPaymentDate() != null)
            expenseHolder.paymentDateTextView.setText(ServerRequestsService.DATE_FORMAT.format(expense.getPaymentDate()));
        expenseHolder.noteEditText.setText(expense.getNote());

        // set the type spinner to be not clickable
        expenseHolder.typeSpinner.setEnabled(false);
        expenseHolder.typeSpinner.setClickable(false);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(inflater.getContext(), R.layout.view_drop_down_item, Expense.EXPENSE_TYPES);
        expenseHolder.typeSpinner.setAdapter(typesAdapter);
        for (int i = 0; i < Expense.EXPENSE_TYPES.length; i++) {
            if (Expense.EXPENSE_TYPES[i].equals(expense.getType())) {
                expenseHolder.typeSpinner.setSelection(i);
                break;
            }
        }

        expenseHolder.enterEditModeBtn.setOnClickListener(v -> enterEditMode(expenseHolder, expense, position));

        expenseHolder.deleteExpenseBtn.setOnClickListener(v ->
                ServerRequestsService.getInstance().deleteExpense(
                        expense.getId(),
                        () -> { // filter if need and notify
                            if (filterMethod != null) {
                                this.expenses.remove(position);
                            }
                            activity.runOnUiThread(() -> notifyItemRemoved(position));
                        }));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    /**
     * enter to edit mode - can edit the fields of the expense
     */
    @SuppressLint("ResourceAsColor")
    void enterEditMode(@NonNull ExpenseHolder holder, Expense expense, int position) {
        Expense temp = new Expense(expense);
        holder.enterEditModeBtn.setVisibility(View.INVISIBLE);
        holder.deleteExpenseBtn.setVisibility(View.INVISIBLE);

        holder.editTitleBtn.setVisibility(View.VISIBLE);
        holder.editTypeBtn.setVisibility(View.VISIBLE);
        holder.editAmountBtn.setVisibility(View.VISIBLE);
        holder.editPaymentDateBtn.setVisibility(View.VISIBLE);
        holder.editNoteBtn.setVisibility(View.VISIBLE);
        holder.saveChangesBtn.setVisibility(View.VISIBLE);
        holder.dismissChangesBtn.setVisibility(View.VISIBLE);

        // set on click to the buttons for the edit text
        setOnClickOnBtnToEditText(holder.titleEditText, holder.editTitleBtn, holder.defaultEditTextForBackground.getBackground());
        setOnClickOnBtnToEditText(holder.amountEditText, holder.editAmountBtn, holder.defaultEditTextForBackground.getBackground());
        setOnClickOnBtnToEditText(holder.noteEditText, holder.editNoteBtn, holder.defaultEditTextForBackground.getBackground());

        holder.editPaymentDateBtn.setOnClickListener(v -> { // open the dialog for pick the date
            Calendar calendar = Calendar.getInstance();
            if (temp.getPaymentDate() != null)
                calendar.setTime(temp.getPaymentDate());
            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                temp.setPaymentDate(calendar.getTime());
                holder.paymentDateTextView.setText(ServerRequestsService.DATE_FORMAT.format(temp.getPaymentDate()));
            };
            new DatePickerDialog(inflater.getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // set op click for the edit type button
        AtomicBoolean isTypeClicked = new AtomicBoolean(false);
        holder.editTypeBtn.setOnClickListener(v -> {
            if (!isTypeClicked.get()) {
                isTypeClicked.set(true);
                holder.editTypeBtn.setImageResource(R.drawable.ic_baseline_close_24);
                holder.editTypeBtn.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.close_button)));
                holder.typeSpinner.setEnabled(true);
                holder.typeSpinner.setClickable(true);
                holder.typeSpinner.performClick();
            } else {
                isTypeClicked.set(false);
                holder.editTypeBtn.setImageResource(R.drawable.ic_baseline_edit_24);
                holder.editTypeBtn.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.done_button)));
                holder.typeSpinner.setEnabled(false);
                holder.typeSpinner.setClickable(false);
            }
        });

        holder.saveChangesBtn.setOnClickListener(v -> exitEditMode(holder, expense, temp, true, position));
        holder.dismissChangesBtn.setOnClickListener(v -> exitEditMode(holder, expense, null, false, position));
    }

    /**
     * set the fab button click listener for get in edit the field
     *
     * @param editText     the edit text for focus on him
     * @param fab          the button
     * @param defaultColor default background for rhe edit text
     */
    @SuppressLint("ResourceAsColor")
    void setOnClickOnBtnToEditText(EditText editText, FloatingActionButton fab, Drawable defaultColor) {
        AtomicBoolean isClicked = new AtomicBoolean(false);
        fab.setOnClickListener(v -> {
            if (!isClicked.get()) {
                isClicked.set(true);
                // the button color and icon
                fab.setImageResource(R.drawable.ic_baseline_close_24);
                fab.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.close_button)));
                // can be clicked
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.setClickable(true);
                // the edit text background
                editText.setBackground(defaultColor);
                // open the keyboard
                openKeyboardFunction.run();
                // focus on the edit text
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            } else {
                isClicked.set(false);
                editText.setBackgroundColor(Color.TRANSPARENT);
                fab.setImageResource(R.drawable.ic_baseline_edit_24);
                fab.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.done_button)));
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setClickable(false);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void exitEditMode(@NonNull ExpenseHolder holder, Expense originalExpense, Expense tempExpense, boolean shouldUpdate, int position) {
        if (shouldUpdate) {
            tempExpense.setType(holder.typeSpinner.getSelectedItem().toString());
            tempExpense.setTitle(holder.titleEditText.getText().toString());
            tempExpense.setAmount(Double.parseDouble(holder.amountEditText.getText().toString()));
            tempExpense.setNote(holder.noteEditText.getText().toString());

            originalExpense.update(tempExpense); // update in the real object
            ServerRequestsService.getInstance().updateExpense(originalExpense); // req for the server

            // filter if need
            if (this.filterMethod != null && !this.filterMethod.test(originalExpense)) {
                this.expenses.removeIf(expense -> originalExpense.getId() == expense.getId());
            }

        } else { // not need to update
            holder.amountEditText.setText(String.valueOf(originalExpense.getAmount()));
            for (int i = 0; i < Expense.EXPENSE_TYPES.length; i++) {
                if (Expense.EXPENSE_TYPES[i].equals(originalExpense.getType())) {
                    holder.typeSpinner.setSelection(i);
                    break;
                }
            }
            if (originalExpense.getPaymentDate() != null)
                holder.paymentDateTextView.setText(ServerRequestsService.DATE_FORMAT.format(originalExpense.getPaymentDate()));
            holder.noteEditText.setText(originalExpense.getNote());
        }

        notifyItemChanged(position);

        // update the visibility of the buttons and edit text
        holder.enterEditModeBtn.setVisibility(View.VISIBLE);
        holder.deleteExpenseBtn.setVisibility(View.VISIBLE);

        holder.editTitleBtn.setVisibility(View.INVISIBLE);
        holder.editTypeBtn.setVisibility(View.INVISIBLE);
        holder.editAmountBtn.setVisibility(View.INVISIBLE);
        holder.editPaymentDateBtn.setVisibility(View.INVISIBLE);
        holder.editNoteBtn.setVisibility(View.INVISIBLE);
        holder.saveChangesBtn.setVisibility(View.INVISIBLE);
        holder.dismissChangesBtn.setVisibility(View.INVISIBLE);

        holder.titleEditText.setBackgroundColor(Color.TRANSPARENT);
        holder.editTitleBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        holder.amountEditText.setBackgroundColor(Color.TRANSPARENT);
        holder.editAmountBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        holder.noteEditText.setBackgroundColor(Color.TRANSPARENT);
        holder.editNoteBtn.setImageResource(R.drawable.ic_baseline_edit_24);

    }

    private static class ExpenseHolder extends RecyclerView.ViewHolder {
        EditText titleEditText;
        TextView creatorNameTextView;
        Spinner typeSpinner;
        EditText amountEditText;
        TextView uploadDateTextView;
        TextView paymentDateTextView;
        EditText noteEditText;

        FloatingActionButton enterEditModeBtn;
        FloatingActionButton deleteExpenseBtn;

        FloatingActionButton editTitleBtn;
        FloatingActionButton editTypeBtn;
        FloatingActionButton editAmountBtn;
        FloatingActionButton editPaymentDateBtn;
        FloatingActionButton editNoteBtn;
        FloatingActionButton saveChangesBtn;
        FloatingActionButton dismissChangesBtn;

        EditText defaultEditTextForBackground; // for getting the background

        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            titleEditText = itemView.findViewById(R.id.expense_title);
            creatorNameTextView = itemView.findViewById(R.id.expense_creator);
            typeSpinner = itemView.findViewById(R.id.expense_type);
            amountEditText = itemView.findViewById(R.id.expense_amount);
            uploadDateTextView = itemView.findViewById(R.id.expense_uplaod_date);
            paymentDateTextView = itemView.findViewById(R.id.expense_payment_date);
            noteEditText = itemView.findViewById(R.id.expense_note);

            enterEditModeBtn = itemView.findViewById(R.id.edit_expense_btn);
            deleteExpenseBtn = itemView.findViewById(R.id.delete_expense_btn);

            editTitleBtn = itemView.findViewById(R.id.edit_title_btn);
            editTypeBtn = itemView.findViewById(R.id.edit_type_btn);
            editAmountBtn = itemView.findViewById(R.id.edit_amount_btn);
            editPaymentDateBtn = itemView.findViewById(R.id.edit_payment_btn);
            editNoteBtn = itemView.findViewById(R.id.edit_note_btn);
            saveChangesBtn = itemView.findViewById(R.id.save_changes_btn);
            dismissChangesBtn = itemView.findViewById(R.id.dismiss_changes_btn);

            defaultEditTextForBackground = itemView.findViewById(R.id.for_background);

        }


    }
}
