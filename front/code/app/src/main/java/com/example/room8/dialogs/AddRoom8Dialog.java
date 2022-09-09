package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddRoom8Dialog extends AppCompatDialogFragment {
    View view;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_room8, null);

        // close button
        view.findViewById(R.id.close_dialog_btn).setOnClickListener((v -> dismiss()));

        EditText input = view.findViewById(R.id.email_or_username_input);
        TextView errorMsgTextView = view.findViewById(R.id.error_msg_TextView);
        FloatingActionButton submitBtn = view.findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(v -> {
            String emailOrUsername = input.getText().toString();
            if (emailOrUsername.trim().length() ==0){
                errorMsgTextView.setText("enter email or username");
                errorMsgTextView.setVisibility(View.VISIBLE);
                return;
            }
            ServerRequestsService.getInstance().sendJoinReq(emailOrUsername, errorMsg -> {
                requireActivity().runOnUiThread(() -> {
                    errorMsgTextView.setText(errorMsg);
                    errorMsgTextView.setVisibility(View.VISIBLE);
                });
            });
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requireActivity().runOnUiThread(()->{
                if (errorMsgTextView.getVisibility() == View.VISIBLE)
                    errorMsgTextView.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }
}
