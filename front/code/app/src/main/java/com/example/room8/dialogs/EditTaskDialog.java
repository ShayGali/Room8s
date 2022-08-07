package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import com.example.room8.database.NodeService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Roommate;
import com.example.room8.model.Task;
import com.example.room8.model.User;

import java.util.Calendar;

public class EditTaskDialog extends AppCompatDialogFragment {

    private TaskDialogListener listener;
    private final Task tempTask;
    private final Task originalTask;


    private TextView creatorName;
    private Spinner taskTypes;
    private TextView createTime;
    private TextView expirationTime;
    private TextView title;
    private TextView note;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public EditTaskDialog(Task task, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.originalTask = task;
        this.tempTask = new Task(task);
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_task_dialog, null);
        initDialogDataFields(view);

        view.findViewById(R.id.task_delete_btn).setOnClickListener(v -> {
            Apartment.getInstance().getTasks().removeIf(task -> task.getId() == tempTask.getId());
            adapter.notifyDataSetChanged();
            listener.delete(tempTask);
            dismiss();
        });
        builder
                .setView(view)
                .setTitle("Edit Task")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    tempTask.setValues(originalTask);
                })
                .setPositiveButton("Edit", (dialog, which) -> {
                    getValuesFromFields();
                    if (originalTask.shouldUpdateTask(tempTask)) {
                        listener.updateTask(tempTask);
                        originalTask.setValues(tempTask);
                        adapter.notifyDataSetChanged();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

    private void getValuesFromFields() {
        tempTask.setTaskType(taskTypes.getSelectedItem().toString());
        tempTask.setTitle(title.getText().toString());
        if (!note.getText().toString().trim().equals(""))
            tempTask.setNote(note.getText().toString());
    }


    private void initDialogDataFields(View view) {
        creatorName = view.findViewById(R.id.dialog_task_creator_name);
        taskTypes = view.findViewById(R.id.dialog_task_types);
        createTime = view.findViewById(R.id.dialog_task_create_time);
        expirationTime = view.findViewById(R.id.dialog_task_expiration_date);
        title = view.findViewById(R.id.dialog_task_title);
        note = view.findViewById(R.id.dialog_task_note);

        if (tempTask.getCreatorId() == User.getInstance().getId()) {
            creatorName.setText(User.getInstance().getUserName());
        } else {
            for (Roommate r : Apartment.getInstance().getRoommates())
                if (r.getId() == tempTask.getCreatorId()) {
                    creatorName.setText(r.getUserName());
                    break;
                }
        }

        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_dwon_item, Task.TASK_TYPES);
        taskTypes.setAdapter(typesAdapter);
        for (int i = 0; i < Task.TASK_TYPES.length; i++) {
            if (Task.TASK_TYPES[i].equals(tempTask.getTaskType())) {
                taskTypes.setSelection(i);
            }
        }

        createTime.setText(NodeService.DATE_TIME_FORMAT.format(tempTask.getCreateDate()));
        expirationTime.setText(tempTask.getExpirationDate() != null ? NodeService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()) : "set expiration date");

        expirationTime.setOnClickListener(this::setDateTimeDialogs);


        if (tempTask.getTitle() != null)
            title.setText(tempTask.getTitle());

        if (tempTask.getNote() != null) {
            System.out.println(tempTask);
            System.out.println(tempTask.getNote());
            System.out.println(tempTask.getNote() == null);
            note.setText(tempTask.getNote());
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (TaskDialogListener) context;
    }

    public interface TaskDialogListener {
        void updateTask(Task t);

        void delete(Task t);
    }

    public void setDateTimeDialogs(View v) {
        Calendar calendar = Calendar.getInstance();
        if (tempTask.getExpirationDate() != null)
            calendar.setTime(tempTask.getExpirationDate());
        TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTime.setText(NodeService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTime.setText(NodeService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
