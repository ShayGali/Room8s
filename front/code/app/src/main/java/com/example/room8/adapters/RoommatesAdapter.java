package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.model.Apartment;
import com.example.room8.model.Roommate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoommatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Roommate> room8s;
    private final Consumer<Integer> action;

    public RoommatesAdapter(LayoutInflater inflater, List<Roommate> room8s, Consumer<Integer> action) {
        this.inflater = inflater;
        this.room8s = Apartment.getInstance().getRoommates();
        this.action = action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Room8Holder(inflater.inflate(R.layout.view_roomate, parent, false));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Roommate roommate = room8s.get(position);
        Room8Holder room8Holder = (Room8Holder) holder;

        room8Holder.name.setText(roommate.getUserName());
        room8Holder.role.setText("Role: " + roommate.getLevelName());
        room8Holder.delete.setOnClickListener(v -> {
            action.accept(roommate.getId());
            Apartment.getInstance().getRoommates().removeIf(r -> roommate.getId() == r.getId());
            room8s = Apartment.getInstance().getRoommates();
            notifyDataSetChanged();
        });
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
