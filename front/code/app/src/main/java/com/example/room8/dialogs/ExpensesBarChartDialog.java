package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.room8.R;
import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;
import com.example.room8.model.Roommate;
import com.example.room8.model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class ExpensesBarChartDialog extends AppCompatDialogFragment {
    View view;
    Apartment apartment;
    TextView totalExpenses;
    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    Chip chip_room8s;
    Chip chip_types;
    Chip chip_dates;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_expenses_chart, null);

        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);

        totalExpenses = view.findViewById(R.id.total_num);
        apartment = Apartment.getInstance();
        totalExpenses.setText(apartment.getSumExpenses() + "");

        chip_room8s = view.findViewById(R.id.chip_room8ts);
        chip_types = view.findViewById(R.id.chip_types);
        chip_dates = view.findViewById(R.id.chip_dates);


        getBarChart();

        return dialog;
    }

    public ExpensesBarChartDialog(){}

    private void getBarChart() {
        barChart = view.findViewById(R.id.expenses_bar_chart);
        if (barDataSet == null) {
            HashMap<String, Object> hashMap = getRoom8tsExpensesData();
            setBarDataSet((ArrayList) hashMap.get("expensesBarArrayList"),(String[])hashMap.get("stackLabels"));
            barChart.invalidate();
        }

        chip_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = getDatesExpensesData();
                setBarDataSet((ArrayList) hashMap.get("expensesBarArrayList"), (String[]) hashMap.get("stackLabels"));
                barChart.invalidate();
            }
        });

        chip_room8s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = getRoom8tsExpensesData();
                setBarDataSet((ArrayList) hashMap.get("expensesBarArrayList"), (String[]) hashMap.get("stackLabels"));
                barChart.invalidate();
            }
        });

        chip_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = getTypesExpensesData();
                setBarDataSet((ArrayList) hashMap.get("expensesBarArrayList"), (String[]) hashMap.get("stackLabels"));
                barChart.invalidate();
            }
        });

    }

    private void setBarDataSet(ArrayList expensesBarArrayList, String[] stackLabels) {
        for (String x : stackLabels) {
            System.out.println(x);
        }

        this.barDataSet = new BarDataSet(expensesBarArrayList, "");
        this.barDataSet.setDrawIcons(true);

        XAxis xAxis = this.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(stackLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);

        // Color bar data set
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(true);

        this.barData = new BarData(barDataSet);
        this.barChart.setData(barData);
        this.barChart.setFitBars(true);
        this.barChart.getLegend().setEnabled(false);
        this.barChart.getDescription().setEnabled(false);


        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(barDataSet.isStacked());
            }
        });
        // Text color
        barDataSet.setBarBorderColor(Color.WHITE);
        // Setting text size
        barDataSet.setValueTextSize(18f);
        barDataSet.setValueTextColors(ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS));

    }

    private HashMap<String, Object> getDatesExpensesData() {
        HashMap<String, Object> retHashMap = new HashMap<>();
        ArrayList<String> stackLabelsTemp = new ArrayList<>();

        ArrayList expensesBarArrayList = new ArrayList();

        ArrayList<Expense> expenses = Apartment.getInstance().getExpenses();
        HashMap<String, Date> checkedDatesMap = new HashMap<>();

        float barIndex = 0;

        for (int i = 0; i < expenses.size(); i++) {
            double sum = 0;
            for (int j = i + 1; j < expenses.size(); j++) {
                if (!checkedDatesMap.isEmpty())
                    if (checkedDatesMap.containsKey(expenses.get(i).getPaymentDate() + ""))
                        break;
                if (expenses.get(i).getPaymentDate().equals(expenses.get(j).getPaymentDate()))
                    sum += expenses.get(j).getAmount();
            }
            if (checkedDatesMap.containsKey(expenses.get(i).getPaymentDate() + ""))
                continue;

            checkedDatesMap.put(expenses.get(i).getPaymentDate() + "", expenses.get(i).getPaymentDate());
            sum += expenses.get(i).getAmount();

            if (sum != 0) {
                expensesBarArrayList.add(new BarEntry(barIndex++, (float) sum));
                stackLabelsTemp.add(dateBarChartHandler(expenses.get(i).getPaymentDate()+""));
            }
        }


        String[] stackLabels = stackLabelsTemp.toArray(new String[stackLabelsTemp.size()]);


        retHashMap.put("stackLabels",stackLabels);
        retHashMap.put("expensesBarArrayList",expensesBarArrayList);

        return retHashMap;
    }

    private HashMap<String, Object> getRoom8tsExpensesData() {
        HashMap<String, Object> retHashMap = new HashMap<>();
        ArrayList<String> stackLabelsTemp = new ArrayList<>();
        ArrayList expensesBarArrayList = new ArrayList();
        ArrayList<Roommate> roommates = Apartment.getInstance().getRoommates();
        ArrayList<Expense> expenses = Apartment.getInstance().getExpenses();

        float barIndex = 0;

        //Adding room8ts's expenses amount
        double sum = 0;
        for (int i = 0; i < Apartment.getInstance().getRoommates().size(); i++) {
            for (int j = 0; j < Apartment.getInstance().getExpenses().size(); j++) {
                if (roommates.get(i).getId() == expenses.get(j).getUserId()) {
                    sum += expenses.get(j).getAmount();
                }
            }

            if (sum != 0) {
                stackLabelsTemp.add(roommates.get(i).getUserName());
                expensesBarArrayList.add(new BarEntry(barIndex++, (float) sum));
            }

            sum = 0;
        }
        //Adding Self user's expenses amount
        sum = 0;
        for (int j = 0; j < Apartment.getInstance().getExpenses().size(); j++) {
            if (User.getInstance().getId() == expenses.get(j).getUserId())
                sum += expenses.get(j).getAmount();
        }
        if (sum != 0) {
            stackLabelsTemp.add(User.getInstance().getUserName());
            expensesBarArrayList.add(new BarEntry(barIndex++, (float) sum));
        }

        String[] stackLabels = stackLabelsTemp.toArray(new String[stackLabelsTemp.size()]);

        retHashMap.put("stackLabels", stackLabels);
        retHashMap.put("expensesBarArrayList", expensesBarArrayList);

        return retHashMap;
    }

    private HashMap<String, Object> getTypesExpensesData() {
        HashMap<String, Object> retHashMap = new HashMap<>();
        ArrayList<String> stackLabelsTemp = new ArrayList<>();

        ArrayList expensesBarArrayList = new ArrayList();
        ArrayList<Expense> expenses = Apartment.getInstance().getExpenses();
        String[] types = Expense.EXPENSE_TYPES;
        float barIndex = 0;
        for (int i = 0; i < types.length; i++) {
            double sum = 0;
            for (int j = 0; j < expenses.size(); j++) {
                if (types[i].equals(expenses.get(j).getType())) {
                    sum += expenses.get(j).getAmount();
                    if (!stackLabelsTemp.contains(expenses.get(j).getType()))
                        stackLabelsTemp.add(expenses.get(j).getType());
                }
            }
            if (sum != 0)
                expensesBarArrayList.add(new BarEntry(barIndex++, (float) sum));
        }

        String[] stackLabels = stackLabelsTemp.toArray(new String[stackLabelsTemp.size()]);

        retHashMap.put("stackLabels", stackLabels);
        retHashMap.put("expensesBarArrayList", expensesBarArrayList);
        return retHashMap;
    }

    private String dateBarChartHandler(@NotNull String dateToFix){
        String fixedDate = "";
        String[] splitArr = dateToFix.split(" ");

        fixedDate += splitArr[0] +" "+ splitArr[2]+" "+ splitArr[1]+" "+ splitArr[5];

        return fixedDate;
    }


}

