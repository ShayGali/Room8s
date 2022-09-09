package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.model.Apartment;
import com.example.room8.model.Roommate;
import com.example.room8.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.Consumer;

public class RoommatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Roommate> room8s;
    private final Consumer<Integer> action;

    public RoommatesAdapter(LayoutInflater inflater, Consumer<Integer> action) {
        this.inflater = inflater;
        this.room8s = Apartment.getInstance().getRoommates();
        this.action = action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Room8Holder(inflater.inflate(R.layout.view_roomate, parent, false));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Roommate roommate = room8s.get(position);
        Room8Holder room8Holder = (Room8Holder) holder;

        room8Holder.name.setText(roommate.getUserName());

        room8Holder.roleTextView.setText(roommate.getLevelName());

        if (User.getInstance().getUserLevel() == 1){
            room8Holder.editRole.setVisibility(View.INVISIBLE);
            room8Holder.delete.setVisibility(View.GONE);
            return;
        }
        room8Holder.editRole.setOnClickListener(v->{
            if (room8Holder.roleTextView.getVisibility() == View.VISIBLE){
                room8Holder.roleTextView.setVisibility(View.INVISIBLE);
                room8Holder.roleSpinner.setVisibility(View.VISIBLE);
                room8Holder.editRole.setImageResource(R.drawable.ic_baseline_done_24);
                room8Holder.roleSpinner.performClick();
            }else {
                room8Holder.roleTextView.setVisibility(View.VISIBLE);
                room8Holder.roleSpinner.setVisibility(View.INVISIBLE);
                room8Holder.editRole.setImageResource(R.drawable.ic_baseline_edit_24);
                if (!room8Holder.roleSpinner.getSelectedItem().toString().equals(roommate.getLevelName())){
                    ServerRequestsService.getInstance().setRole(roommate.getId(),room8Holder.roleSpinner.getSelectedItem().toString());
                }
            }
        });
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(inflater.getContext(), R.layout.view_drop_down_item, Roommate.LEVELS);
        room8Holder.roleSpinner.setAdapter(roleAdapter);
        for (int i = 0; i < Roommate.LEVELS.length; i++) {
            if (Roommate.LEVELS[i].equals(roommate.getLevelName())) {
                room8Holder.roleSpinner.setSelection(i);
                break;
            }
        }
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
        TextView roleTextView;
        Spinner roleSpinner;
        FloatingActionButton delete;
        FloatingActionButton editRole;

        public Room8Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            roleTextView = itemView.findViewById(R.id.role_TextView);
            roleSpinner = itemView.findViewById(R.id.role_spinner);
            delete = itemView.findViewById(R.id.delete);
            editRole = itemView.findViewById(R.id.edit_role);
        }
    }
}
