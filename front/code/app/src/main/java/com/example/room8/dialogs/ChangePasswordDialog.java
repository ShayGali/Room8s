package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

public class ChangePasswordDialog extends AppCompatDialogFragment {
    Consumer<String> action;

    public ChangePasswordDialog(Consumer<String> action) {
        this.action = action;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);

        TextView errorMsgTextView = view.findViewById(R.id.error_msg_password);
        EditText passwordEditText = view.findViewById(R.id.new_password_editText);
        EditText confirmPasswordNameEditText = view.findViewById(R.id.confirm_new_password_editText);
        FloatingActionButton submitBtn= view.findViewById(R.id.submit_change_password_btn);
        FloatingActionButton closeDialogBtn = view.findViewById(R.id.close_dialog_btn);

        errorMsgTextView.setVisibility(View.GONE);
        errorMsgTextView.setText("");

        submitBtn.setOnClickListener(v -> {
            String password1 = passwordEditText.getText().toString();
            String password2 = confirmPasswordNameEditText.getText().toString();
            if (!MainActivity.isStrongPassword(password1)){
                errorMsgTextView.setVisibility(View.VISIBLE);
                errorMsgTextView.setText("Password not strong enough");
            }
            else if (!password1.equals(password2)){
                errorMsgTextView.setVisibility(View.VISIBLE);
                errorMsgTextView.setText("Passwords don't match");
            }else {
                action.accept(password1);
                dismiss();
            }
        });

        closeDialogBtn.setOnClickListener(v -> dismiss());

        return dialog;
    }


}
