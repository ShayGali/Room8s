package com.example.room8.dialogs;

import com.example.room8.model.Task;

public interface TaskDialogListener {
    void updateTask(Task t);

    void deleteTask(int taskId);

    void addTask(Task tempTask);
}
