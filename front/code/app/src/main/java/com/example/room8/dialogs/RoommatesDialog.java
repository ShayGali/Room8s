package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.room8.adapters.RoommatesAdapter;
import com.example.room8.model.Roommate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.Consumer;

public class RoommatesDialog extends AppCompatDialogFragment {
    List<Roommate> roommates;
    Consumer<Integer> action;

    public RoommatesDialog(List<Roommate> roommates, Consumer<Integer> action) {
        this.roommates = roommates;
        this.action = action;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_recycler_view, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RoommatesAdapter adapter = new RoommatesAdapter(getLayoutInflater(), roommates, action);

        view.findViewById(R.id.close_dialog_btn).setOnClickListener((v->dismiss()));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }
}
