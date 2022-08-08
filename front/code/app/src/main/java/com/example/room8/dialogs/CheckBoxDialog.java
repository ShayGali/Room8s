package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.CheckBoxRecyclerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CheckBoxDialog extends AppCompatDialogFragment {
    ArrayList<String> list;
    ArrayList<Boolean> isChecklist;
    String title;
    Consumer<ArrayList<String>> action;

    public CheckBoxDialog(String title,ArrayList<String> list,  ArrayList<Boolean> isChecklist,Consumer<ArrayList<String>> action) {
        this.list = list;
        this.isChecklist = isChecklist;
        this.title = title;
        this.action = action;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_recycler_view, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CheckBoxRecyclerAdapter adapter = new CheckBoxRecyclerAdapter(getLayoutInflater(), list,isChecklist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        builder.setView(view).setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        ArrayList<String> checked = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            if (isChecklist.get(i))
                checked.add(list.get(i));

        action.accept(checked);
    }
}
