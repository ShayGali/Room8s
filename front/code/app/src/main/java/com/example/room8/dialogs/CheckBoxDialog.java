package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.CheckBoxRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CheckBoxDialog extends AppCompatDialogFragment {
    ArrayList<String> list;
    ArrayList<Boolean> isChecklist;
    List<Boolean> prevIsChecklist;
    String title;
    Consumer<ArrayList<String>> action;
    boolean needSave = false;

    public CheckBoxDialog(String title, ArrayList<String> list, ArrayList<Boolean> isChecklist, Consumer<ArrayList<String>> action) {
        this.list = list;
        this.isChecklist = isChecklist;
        this.prevIsChecklist = new ArrayList<>(isChecklist);
        this.title = title;
        this.action = action;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_recycler_view, null);

        view.findViewById(R.id.close_dialog_btn).setOnClickListener(v -> {
            needSave = false;
            dismiss();
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        CheckBoxRecyclerAdapter adapter = new CheckBoxRecyclerAdapter(getLayoutInflater(), list, isChecklist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((TextView) view.findViewById(R.id.recycler_title)).setText(title);

        FloatingActionButton okBtn = view.findViewById(R.id.ok_btn);
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setOnClickListener(v -> {
            needSave = true;
            dismiss();
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (needSave) {
            ArrayList<String> checked = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
                if (isChecklist.get(i))
                    checked.add(list.get(i));
            action.accept(checked);
        } else {
            for (int i = 0; i < isChecklist.size(); i++) {
                isChecklist.set(i, prevIsChecklist.get(i));
            }
        }
    }
}
