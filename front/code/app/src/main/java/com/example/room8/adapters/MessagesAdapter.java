package com.example.room8.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.model.Message;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;

    private LayoutInflater inflater;
    private List<Message> messages;


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

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);
        if (messages.get(position).getName().equals("Yossi")) {
            SentMessageHolder sentMessageHolder = (SentMessageHolder) holder;
            sentMessageHolder.messageContent.setText(m.getTxt());

        } else {
            ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
            receivedMessageHolder.messageContent.setText(m.getTxt());
            receivedMessageHolder.senderName.setText(m.getName());
            receivedMessageHolder.senderImg.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getName().equals("Yossi"))
            return TYPE_MESSAGE_SENT;
        return TYPE_MESSAGE_RECEIVED;
    }

    public void addMessage(Message message){
        messages.add(message);

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
