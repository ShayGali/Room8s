package com.example.room8.dialogs;

import android.annotation.SuppressLint;
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

import com.example.room8.ImageFactory;
import com.example.room8.R;
import com.example.room8.adapters.ChangeProfileImgAdapter;

public class ChangeProfileImgDialog extends AppCompatDialogFragment {
    View view;

    @SuppressLint("InflateParams")
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_change_profile_img, null);

        view.findViewById(R.id.close_dialog_btn).setOnClickListener(v -> dismiss());
        initAnimalsRecyclerView();
        initManRecyclerView();
        initWomanRecyclerView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }


    private void initAnimalsRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.animal_recyclerView);
        ChangeProfileImgAdapter adapter = new ChangeProfileImgAdapter(getLayoutInflater(), requireContext(), ImageFactory.ungenderAndAnimalsImgs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initManRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.man_recyclerView);
        ChangeProfileImgAdapter adapter = new ChangeProfileImgAdapter(getLayoutInflater(), requireContext(), ImageFactory.manImgs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initWomanRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.woman_recyclerView);
        ChangeProfileImgAdapter adapter = new ChangeProfileImgAdapter(getLayoutInflater(), requireContext(), ImageFactory.womanImgs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}
