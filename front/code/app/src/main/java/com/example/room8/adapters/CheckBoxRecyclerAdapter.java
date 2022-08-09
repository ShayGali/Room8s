package com.example.room8.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;

import java.util.ArrayList;

public class CheckBoxRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<String> list;
    private ArrayList<Boolean> isCheckedList;

    public CheckBoxRecyclerAdapter(LayoutInflater inflater, ArrayList<String> list, ArrayList<Boolean> checked) {
        this.inflater = inflater;
        this.list = list;
        this.isCheckedList = checked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_drop_down_item_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.checkBox.setText(list.get(position));
        viewHolder.checkBox.setChecked(isCheckedList.get(position));
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonView.setChecked(isChecked);
            isCheckedList.set(position,isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.drop_down_checkbox);
        }
    }
}

