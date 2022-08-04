package com.example.room8.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.example.room8.R;
import com.example.room8.database.NodeService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Task;
import com.example.room8.model.User;

import java.util.Arrays;
import java.util.Collections;

public class TaskDialog extends AppCompatDialogFragment {

    private TaskDialogListener listener;
    private final Task task;

    private TextView creatorName;
    private Spinner taskTypes;
    private TextView createTime;
    private TextView expirationTime;
    private TextView title;
    private TextView note;

    public TaskDialog(Task task) {
        this.task = task;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.task_dialog, null);
        initDialogDataFields(view);

        builder
                .setView(view)
                .setTitle("title")
                .setNegativeButton("cencel", (dialog, which) -> {
                })
                .setPositiveButton("ok", (dialog, which) -> {
                    listener.updateTask(task);
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

    private void initDialogDataFields(View view) {
        creatorName = view.findViewById(R.id.dialog_task_creator_name);
        taskTypes = view.findViewById(R.id.dialog_task_types);
        createTime = view.findViewById(R.id.dialog_task_create_time);
        expirationTime = view.findViewById(R.id.dialog_task_expiration_date);
        title = view.findViewById(R.id.dialog_task_title);
        note = view.findViewById(R.id.dialog_task_note);

        if (task.getCreatorId() == User.getInstance().getId()) {
            creatorName.setText(User.getInstance().getUserName());
        } else {
            Apartment.getInstance().getRoommates().stream().filter(roommate -> roommate.getId() == task.getCreatorId()).findFirst().ifPresent(roommate -> creatorName.setText(roommate.getUserName()));
        }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.drop_dwon_item, Task.TASK_TYPES);
        taskTypes.setAdapter(adapter);

        createTime.setText(NodeService.DATE_TIME_FORMAT.format(task.getCreateDate()));
        expirationTime.setText(task.getExpirationDate() != null ? NodeService.DATE_TIME_FORMAT.format(task.getExpirationDate()) : "set expiration date");
        if (task.getTitle() != null)
            title.setText(task.getTitle());
        if (task.getNote() != null)
            note.setText(task.getNote());
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (TaskDialogListener) context;
    }

    public interface TaskDialogListener {
        void updateTask(Task t);
    }


}
