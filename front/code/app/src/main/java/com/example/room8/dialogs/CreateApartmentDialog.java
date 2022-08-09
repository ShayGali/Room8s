package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.room8.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

public class CreateApartmentDialog extends AppCompatDialogFragment {
    Consumer<String> actionCallback;

    public CreateApartmentDialog(Consumer<String> actionCallback) {
        this.actionCallback = actionCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_apartment, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);


        EditText apartmentNameEditText = view.findViewById(R.id.apartment_name_EditText);
        FloatingActionButton createApartmentBtn = view.findViewById(R.id.create_apartment_dialog_btn);
        FloatingActionButton closeDialogBtn = view.findViewById(R.id.close_dialog_btn);

        createApartmentBtn.setOnClickListener(v -> {
            String apartmentName = apartmentNameEditText.getText().toString();
            if (apartmentName.trim().equals(""))
                Toast.makeText(getContext(), "Enter Name", Toast.LENGTH_SHORT).show();
            else {
                actionCallback.accept(apartmentName);
                dismiss();
            }
        });


        closeDialogBtn.setOnClickListener(v -> dismiss());
        return dialog;
    }
}
