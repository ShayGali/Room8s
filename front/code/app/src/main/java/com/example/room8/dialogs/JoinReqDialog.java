package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.adapters.JoinReqAdapter;
import com.example.room8.database.ServerRequestsService;

import org.json.JSONArray;

public class JoinReqDialog extends AppCompatDialogFragment {
    View view;
    JSONArray jsonArray;
    Runnable navigateFunction;

    public JoinReqDialog(JSONArray jsonArray, Runnable navigateFunction) {
        this.jsonArray = jsonArray;
        this.navigateFunction = navigateFunction;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_recycler_view, null);

        // close button
        view.findViewById(R.id.close_dialog_btn).setOnClickListener((v -> dismiss()));

        ((TextView) view.findViewById(R.id.recycler_title)).setText("You have " + jsonArray.length() + " invite requests");
        // init
        initRecyclerView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        @SuppressLint("SetTextI18n") JoinReqAdapter adapter = new JoinReqAdapter(requireActivity().getLayoutInflater(), jsonArray,
                (apartmentId, join) -> ServerRequestsService.getInstance().handleJoinReq(apartmentId, join, navigateFunction),
                length -> {
                    if (length == 0) dismiss();
                    requireActivity().runOnUiThread(() -> ((TextView) view.findViewById(R.id.recycler_title)).setText("You have " + jsonArray.length() + " invite requests"));
                }
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
