package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExpensesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private ArrayList<Expense> expenses;
    private Predicate<Expense> filterMethod;

    public ExpensesAdapter(LayoutInflater inflater, Predicate<Expense> filterMethod) {
        this.inflater = inflater;
        this.filterMethod = filterMethod;
        this.expenses = (ArrayList<Expense>) Apartment.getInstance().getExpenses().stream().filter(filterMethod).collect(Collectors.toList());
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
        expenseHolder.type.setText(expense.getType());
        expenseHolder.amount.setText(expense.getAmount() + "");
        if (expense.getUploadDate() != null)
            expenseHolder.uploadDate.setText(ServerRequestsService.DATE_FORMAT.format(expense.getUploadDate()));
        if (expense.getPaymentDate() != null)
            expenseHolder.paymentDate.setText(ServerRequestsService.DATE_FORMAT.format(expense.getPaymentDate()));
        expenseHolder.note.setText(expense.getNote());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    private static class ExpenseHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView creatorName;
        TextView type;
        TextView amount;
        TextView uploadDate;
        TextView paymentDate;
        TextView note;
        FloatingActionButton edit;

        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expense_title);
            creatorName = itemView.findViewById(R.id.expense_cretor);
            type = itemView.findViewById(R.id.expense_type);
            amount = itemView.findViewById(R.id.expense_amount);
            uploadDate = itemView.findViewById(R.id.expense_uplaod_date);
            paymentDate = itemView.findViewById(R.id.expense_patment_date);
            note = itemView.findViewById(R.id.expense_note);
            edit = itemView.findViewById(R.id.edit_expense_btn);
        }
    }
}
