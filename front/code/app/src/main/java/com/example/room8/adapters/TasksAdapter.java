package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.dialogs.EditTaskDialog;
import com.example.room8.model.Apartment;
import com.example.room8.model.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Task> tasks;
    private final MainActivity activity;

    public TasksAdapter(LayoutInflater inflater, MainActivity activity) {
        this.inflater = inflater;
        this.activity = activity;
        this.tasks = Apartment.getInstance().getTasks();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskHolder(inflater.inflate(R.layout.view_single_task_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        TaskHolder taskHolder = (TaskHolder) holder;
        if (task.getTitle() == null || task.getTitle().trim().equals(""))
            taskHolder.name.setText("task don't have title");
        else
            taskHolder.name.setText(task.getTitle());
        taskHolder.date.setText(task.getExpirationDate() != null ? ServerRequestsService.DATE_FORMAT.format(task.getExpirationDate()) : "task don't have expiration");
        taskHolder.type.setText(task.getTaskType());

        taskHolder.layout.setOnClickListener(v -> {
            EditTaskDialog dialog = new EditTaskDialog(task, activity, this);
            dialog.show(activity.getSupportFragmentManager(), "task dialog");
        });
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addTask(Task task) {
        for (Task t : tasks) {
            if (t.getId() == task.getId()) {
                t.updateTask(task);
                notifyDataSetChanged();
                return;
            }
        }
        tasks.add(task);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTask(JSONObject jsonObject) throws JSONException, ParseException {
        this.addTask(new Task(jsonObject));
    }

    private static class TaskHolder extends RecyclerView.ViewHolder {
        View layout;
        TextView name;
        TextView type;
        TextView date;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.task_layout);
            name = itemView.findViewById(R.id.task_name);
            type = itemView.findViewById(R.id.task_type);
            date = itemView.findViewById(R.id.task_date);
        }
    }
}
