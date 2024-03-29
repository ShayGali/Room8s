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

public class EditTaskDialog extends AppCompatDialogFragment {

    private final Task tempTask;
    private final Task originalTask;
    private final Activity activity;

    private TextView creatorName;
    private TextView associate;
    private Spinner taskTypes;
    private TextView createTime;
    private TextView expirationTime;
    private TextView title;
    private TextView note;

    private final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    ArrayList<String> names;

    public EditTaskDialog(Task task, Activity activity, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.originalTask = task;
        this.tempTask = new Task(task);
        this.activity = activity;
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_task, null);
        initDialogDataFields(view);

        view.findViewById(R.id.task_delete_btn).setOnClickListener(v -> {
            Apartment.getInstance().getTasks().removeIf(task -> task.getId() == tempTask.getId());
            ServerRequestsService.getInstance().deleteTask(tempTask.getId(), () -> activity.runOnUiThread(adapter::notifyDataSetChanged));
            dismiss();
        });
        builder
                .setView(view)
                .setTitle("Edit Task")
                .setNegativeButton("Cancel", (dialog, which) -> tempTask.copyValues(originalTask))
                .setPositiveButton("Edit", (dialog, which) -> {
                    getValuesFromFields();
                    if (originalTask.shouldUpdateTask(tempTask)) {
                        ServerRequestsService.getInstance().updateTask(tempTask);
                        originalTask.copyValues(tempTask);
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


    private void initDialogDataFields(View view) {
        creatorName = view.findViewById(R.id.dialog_task_creator_name);
        associate = view.findViewById(R.id.dialog_task_associate);
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

        names = new ArrayList<>();
        if (tempTask.getExecutorsIds() != null && tempTask.getExecutorsIds().size() != 0) {
            for (Integer id : tempTask.getExecutorsIds()) {
                if (User.getInstance().getId() == id) names.add(User.getInstance().getUserName());
                else names.add(Apartment.getInstance().getRoom8NameById(id));
            }
            associate.setText(String.join(", ", names));
        }

        ArrayList<Roommate> room8 = Apartment.getInstance().getRoommates();
        ArrayList<String> room8Name = new ArrayList<>();
        ArrayList<Boolean> isChecklist = new ArrayList<>();

        if (tempTask.getExecutorsIds() != null && tempTask.getExecutorsIds().size() != 0) {
            if (tempTask.getExecutorsIds().contains(User.getInstance().getId())) {
                isChecklist.add(true);
            } else
                isChecklist.add(false);
        } else {
            isChecklist.add(false);
        }
        room8Name.add(User.getInstance().getUserName());

        for (int i = 0; i < room8.size(); i++) {
            if (tempTask.getExecutorsIds() != null && tempTask.getExecutorsIds().size() != 0) {
                if (tempTask.getExecutorsIds().contains(room8.get(i).getId())) {
                    isChecklist.add(true);
                } else {
                    isChecklist.add(false);
                }
            } else {
                isChecklist.add(false);
            }
            room8Name.add(room8.get(i).getUserName());
        }


        associate.setOnClickListener(v -> {
            @SuppressLint("SetTextI18n") CheckBoxDialog checkBoxDialog = new CheckBoxDialog("Select Executors", room8Name, isChecklist, strings -> {
                names = strings;
                if (strings.size() == 0) {
                    associate.setText("Click To Select");
                } else
                    associate.setText(String.join(", ", strings));
            });
            checkBoxDialog.show(getParentFragmentManager(), "check box dialog");
        });

        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getContext(), R.layout.view_drop_down_item, Task.TASK_TYPES);
        taskTypes.setAdapter(typesAdapter);
        for (int i = 0; i < Task.TASK_TYPES.length; i++) {
            if (Task.TASK_TYPES[i].equals(tempTask.getTaskType())) {
                taskTypes.setSelection(i);
            }
        }

        createTime.setText(ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getCreateDate()));
        expirationTime.setText(tempTask.getExpirationDate() != null ? ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()) : "set expiration date");

        expirationTime.setOnClickListener(this::setDateTimeDialogs);


        if (tempTask.getTitle() != null)
            title.setText(tempTask.getTitle());

        if (tempTask.getNote() != null) {
            note.setText(tempTask.getNote());
        }
    }

    public void setDateTimeDialogs(View v) {
        Calendar calendar = Calendar.getInstance();
        if (tempTask.getExpirationDate() != null)
            calendar.setTime(tempTask.getExpirationDate());
        TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTime.setText(ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            tempTask.setExpirationDate(calendar.getTime());
            expirationTime.setText(ServerRequestsService.DATE_TIME_FORMAT.format(tempTask.getExpirationDate()));
        };
        new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
