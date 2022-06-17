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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;

    private final LayoutInflater inflater;
    private final List<Message> messages;

    private String lastDate;

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
        Message message = messages.get(position);
        AbstractMessageHolder messageHolder = (AbstractMessageHolder) holder;
        if (lastDate == null)
            lastDate = Message.DATE_FORMAT.format(message.getDate());
        else {
            String messageDate = Message.DATE_FORMAT.format(message.getDate());
            System.out.println("prev - " + lastDate);
            System.out.println("now - " + messageDate);
            if (!lastDate.equals(messageDate)) {
                System.out.println("if");
                lastDate = messageDate;
                messageHolder.messageDate.setText(message.getDateFormat());
            }else {
                System.out.println("else");
                messageHolder.messageDate.setVisibility(View.INVISIBLE);
            }
        }

        messageHolder.messageContent.setText(message.getMsgContent());
        messageHolder.messageTime.setText(message.getTimeFormat());
        if (!message.isSent()) {
            ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
            receivedMessageHolder.senderName.setText(message.getUserName());
            receivedMessageHolder.senderImg.setImageResource(message.getIconID());
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isSent())
            return TYPE_MESSAGE_SENT;
        else return TYPE_MESSAGE_RECEIVED;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(JSONObject jsonObject) throws JSONException, ParseException {
        messages.add(new Message(jsonObject));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(Message message) {
        messages.add(message);
        notifyDataSetChanged();
    }


    private static abstract class AbstractMessageHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        TextView messageDate;
        TextView messageTime;

        public AbstractMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageDate = itemView.findViewById(R.id.message_date);
            messageTime = itemView.findViewById(R.id.message_time);
        }
    }

    private static class SentMessageHolder extends AbstractMessageHolder {
        public SentMessageHolder(View view) {
            super(view);
        }
    }

    private static class ReceivedMessageHolder extends AbstractMessageHolder {
        TextView senderName;
        ImageView senderImg;

        public ReceivedMessageHolder(View view) {
            super(view);
            senderName = view.findViewById(R.id.sender_name);
            senderImg = view.findViewById(R.id.sender_img);
        }
    }
}


