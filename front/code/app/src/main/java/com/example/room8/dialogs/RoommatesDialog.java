package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.RoommatesAdapter;
import com.example.room8.model.Roommate;

import java.util.List;
import java.util.function.Consumer;

public class RoommatesDialog extends AppCompatDialogFragment {

    View view;

    List<Roommate> roommates;
    Consumer<Integer> action;

    public RoommatesDialog(List<Roommate> roommates, Consumer<Integer> action) {
        this.roommates = roommates;
        this.action = action;
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_room8s, null);

        // close button
        view.findViewById(R.id.close_dialog_btn).setOnClickListener((v -> dismiss()));

        // init
        initRoom8sRecyclerView();
        initAddRoom8Btn();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

    void initRoom8sRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RoommatesAdapter adapter = new RoommatesAdapter(getLayoutInflater(), roommates, action);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void initAddRoom8Btn() {
        view.findViewById(R.id.add_room8_btn).setOnClickListener(v -> {
            Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show();
        });
    }
}