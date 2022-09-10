package com.example.room8.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JoinReqAdapter extends RecyclerView.Adapter<JoinReqAdapter.JoinReqHolder> {
    private final LayoutInflater inflater;
    private final JSONArray joinReqJsonArray;
    private final BiConsumer<Integer, Boolean> action;
    private final Consumer<Integer> titleUpdate;

    private static final String SENDER_NAME_KEY = "user_name";
    private static final String APARTMENT_NAME_KEY = "apartment_name";
    private static final String APARTMENT_ID_KEY = "apartment_ID";


    public JoinReqAdapter(LayoutInflater inflater, JSONArray joinReqJsonArray, BiConsumer<Integer, Boolean> action, Consumer<Integer> titleUpdate) {
        this.inflater = inflater;
        this.joinReqJsonArray = joinReqJsonArray;
        this.action = action;
        this.titleUpdate = titleUpdate;
    }

    @NonNull
    @Override
    public JoinReqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinReqHolder(inflater.inflate(R.layout.view_join_req, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinReqHolder holder, int position) {
        try {
            JSONObject req = joinReqJsonArray.getJSONObject(position);

            if (req.has(SENDER_NAME_KEY) && !req.isNull(SENDER_NAME_KEY))
                holder.senderName.setText(req.getString(SENDER_NAME_KEY));

            if (req.has(APARTMENT_NAME_KEY) && !req.isNull(APARTMENT_NAME_KEY))
                holder.apartment_name.setText(req.getString(APARTMENT_NAME_KEY));

            if (!req.has(APARTMENT_ID_KEY) || req.isNull(APARTMENT_ID_KEY)) {
                holder.acceptBtn.setEnabled(false);
                holder.rejectBtn.setEnabled(false);
                return;
            }

            int apartmentId = req.getInt(APARTMENT_ID_KEY);

            holder.acceptBtn.setOnClickListener(v -> {
                action.accept(apartmentId, true);
                joinReqJsonArray.remove(position);
                notifyItemRemoved(position);
                titleUpdate.accept(joinReqJsonArray.length());
            });
            holder.rejectBtn.setOnClickListener(v -> {
                action.accept(apartmentId, false);
                joinReqJsonArray.remove(position);
                notifyItemRemoved(position);
                titleUpdate.accept(joinReqJsonArray.length());
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return joinReqJsonArray.length();
    }


    protected static class JoinReqHolder extends RecyclerView.ViewHolder {
        TextView senderName;
        TextView apartment_name;
        FloatingActionButton acceptBtn;
        FloatingActionButton rejectBtn;

        public JoinReqHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name_TextView);
            apartment_name = itemView.findViewById(R.id.apartment_name_TextView_join_req);
            acceptBtn = itemView.findViewById(R.id.accept_req_btn);
            rejectBtn = itemView.findViewById(R.id.reject_req_btn);
        }
    }
}
