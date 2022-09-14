package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Roommate;
import com.example.room8.model.Task;
import com.example.room8.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AddTaskDialog extends AppCompatDialogFragment {
    private final Activity activity;
    private final Task tempTask;
    private TextView executorsTextView;
    private Spinner taskTypesSpinner;
    private TextView expirationTimeTextView;
    private TextView titleTextView;
    private TextView noteTextView;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    ArrayList<String> names;

    public AddTaskDialog(Activity activity, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.activity = activity;
        this.adapter = adapter;
        tempTask = new Task();
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);
        initDialogDataFields(view);

        builder
                .setView(view)
                .setTitle("Add Task")
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setPositiveButton("Add", (dialog, which) -> {
                    getValuesFromFields();
                    ServerRequestsService.getInstance().addTask(tempTask, () -> activity.runOnUiThread(() -> adapter.notifyDataSetChanged()));
                    adapter.notifyDataSetChanged();
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

    private void initDialogDataFields(View view) {
        executorsTextView = view.findViewById(R.id.dialog_task_executors_TextView);
        taskTypesSpinner = view.findViewById(R.id.dialog_task_types);
        expirationTimeTextView = view.findViewById(R.id.dialog_task_expiration_date);
        titleTextView = view.findViewById(R.id.dialog_task_title);
        noteTextView = view.findViewById(R.id.dialog_task_note);

        ArrayList<String> room8Name = (ArrayList<String>) Apartment.getInstance().getRoommates().stream().map(Roommate::getUserName).collect(Collectors.toList());
        room8Name.add(0, User.getInstance().getUserName());
        ArrayList<Boolean> isChecklist = new ArrayList<>();
        for (int i = 0; i < room8Name.size(); i++) {
            isChecklist.add(false);
        }
        executorsTextView.setOnClickListener(v -> {
            CheckBoxDialog checkBoxDialog = new CheckBoxDialog("Select Executors", room8Name, isChecklist, strings -> {
                names = strings;
                if (strings.size() == 0)
                    executorsTextView.setText("Click To Select");
                else
                    executorsTextView.setText(String.join(", ", strings));
            });

            checkBoxDialog.show(getParentFragmentManager(), "check box dialog");
        });

        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), R.layout.view_drop_down_item, Task.TASK_TYPES);
        taskTypesSpinner.setAdapter(typesAdapter);

        expirationTimeTextView.setOnClickListener(this::showDateTimeDialogs);

    }

    private void getValuesFromFields() {
        tempTask.setTaskType(taskTypesSpinner.getSelectedItem().toString());

        if (!titleTextView.getText().toString().trim().equals(""))
            tempTask.setTitle(titleTextView.getText().toString());

        if (!noteTextView.getText().toString().trim().equals(""))
            tempTask.setNote(noteTextView.getText().toString());

        if (names != null) {
            ArrayList<Roommate> room8 = Apartment.getInstance().getRoommates();
            List<Integer> executorsIds = names.stream().map(name -> {
                if (User.getInstance().getUserName().equals(name))
                    return User.getInstance().getId();
                for (Roommate roommate : room8) {
                    if (roommate.getUserName().equals(name))
                        return roommate.getId();
                }
                return null;
            }).collect(Collectors.toList());
            tempTask.setExecutorsIds(executorsIds);
        }
    }


    private void showDateTimeDialogs(View v) {
        Calendar calendar = Calendar.getInstance();
        if (tempTask.getExpirationDate() != null)
            calendar.setTime(tempTask.getExpirationDate());
        TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTimeTextView.setText(ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTimeTextView.setText(ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


}
