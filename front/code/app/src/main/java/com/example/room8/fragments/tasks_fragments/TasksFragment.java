package com.example.room8.fragments.tasks_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.adapters.TasksAdapter;
import com.example.room8.dialogs.AddTaskDialog;

public class TasksFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        MainActivity activity = (MainActivity) requireActivity();

        RecyclerView recyclerView = view.findViewById(R.id.tasks_RecyclerView);
        TasksAdapter adapter = new TasksAdapter(getLayoutInflater(), (MainActivity) requireActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        activity.fetchTasks(adapter::notifyDataSetChanged);

        View addTaskButton = view.findViewById(R.id.add_task_btn);
        
        addTaskButton.setOnClickListener(v -> {
            AddTaskDialog addTaskDialog = new AddTaskDialog(activity, adapter);
            addTaskDialog.show(activity.getSupportFragmentManager(), "Add New Task");
        });
        return view;
    }
}