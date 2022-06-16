package com.example.room8.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;

    private LayoutInflater inflater;
    private List<JSONObject> messages;


    public MessagesAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        messages = new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.message_sent, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.message_received, parent, false);
                return new ReceivedMessageHolder(view);
        }
        view = inflater.inflate(R.layout.message_received, parent, false);
        return new ReceivedMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = messages.get(position);
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            if (message.getBoolean("isSent")){
                if (message.has("message")) {
                    SentMessageHolder sentMessageHolder = (SentMessageHolder) holder;
                    sentMessageHolder.messageContent.setText(message.getString("message"));
                    sentMessageHolder.messageTime.setText(message.getString("timestamp"));
                    System.out.println(message.getString("timestamp"));
                }
            }else {
                if (message.has("message")) {
                    ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
                    receivedMessageHolder.messageContent.setText(message.getString("message"));
                    receivedMessageHolder.senderName.setText(message.getString("user_name"));
                    receivedMessageHolder.senderImg.setImageResource(R.drawable.ic_launcher_foreground);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message"))
                    return TYPE_MESSAGE_SENT;
            } else {
                if (message.has("message"))
                    return TYPE_MESSAGE_RECEIVED;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(JSONObject jsonObject){
        messages.add(jsonObject);
        notifyDataSetChanged();
    }



    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView messageDate;
        TextView messageTime;

        public SentMessageHolder(View view) {
            super(view);
            messageContent = view.findViewById(R.id.message_content);
            messageDate = view.findViewById(R.id.message_date);
            messageTime = view.findViewById(R.id.message_time);
        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView messageDate;
        TextView messageTime;
        TextView senderName;
        ImageView senderImg;

        public ReceivedMessageHolder(View view) {
            super(view);
            messageContent = view.findViewById(R.id.message_content);
            messageDate = view.findViewById(R.id.message_date);
            messageTime = view.findViewById(R.id.message_time);
            senderName = view.findViewById(R.id.sender_name);
            senderImg = view.findViewById(R.id.sender_img);
        }
    }
}
