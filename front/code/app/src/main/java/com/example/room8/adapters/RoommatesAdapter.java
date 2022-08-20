package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.model.Roommate;
import com.example.room8.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RoommatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Roommate> room8s;

    public RoommatesAdapter(LayoutInflater inflater, List<Roommate> room8s) {
        this.inflater = inflater;
        this.room8s = room8s;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Room8Holder(inflater.inflate(R.layout.view_roomate, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Roommate roommate = room8s.get(position);
        Room8Holder room8Holder = (Room8Holder) holder;

        room8Holder.name.setText(roommate.getUserName());
        room8Holder.role.setText("Role: " + roommate.getLevelName());
        room8Holder.delete.setOnClickListener(v->{});
    }

    @Override
    public int getItemCount() {
        return room8s.size();
    }


    private static class Room8Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView role;
        FloatingActionButton delete;

        public Room8Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
