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
import java.util.UUID;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;

    private final LayoutInflater inflater;
    private final List<Message> messages;

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
                view = inflater.inflate(R.layout.view_message_sent, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.view_message_received, parent, false);
                return new ReceivedMessageHolder(view);
        }
        view = inflater.inflate(R.layout.view_message_received, parent, false);
        return new ReceivedMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        AbstractMessageHolder messageHolder = (AbstractMessageHolder) holder;

        messageHolder.layout.setOnLongClickListener(view -> { //TODO display dialog for delete message
            System.out.println(message.toString());
            return false;
        });

        messageHolder.messageDate.setText(message.getDateFormat());
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

    /**
     * get the type of the view in the position
     *
     * @param position the position in the list
     * @return int number that represent the view type <br>
     * return {@value TYPE_MESSAGE_SENT} -> for sent message<br>
     * return {@value TYPE_MESSAGE_RECEIVED} -> for received message<br>
     */
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isSent())
            return TYPE_MESSAGE_SENT;
        else return TYPE_MESSAGE_RECEIVED;
    }

    /**
     * add a message for the list of messages
     *
     * @param jsonObject json object, and convert it to Message object
     * @throws JSONException  get the keys from the json object
     * @throws ParseException parse the date
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(JSONObject jsonObject) throws JSONException, ParseException {
        this.addMessage(new Message(jsonObject));
    }

    /**
     * get a message and add it to the list
     *
     * @param message message object
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(Message message) {
        messages.add(message);
        messages.sort(message);
        notifyDataSetChanged();
    }

    public void setMessageIdByUUID(int insertedId, String uuid) {
        messages.stream()
                .filter(message ->
                        UUID.fromString(uuid).equals(message.getUuid())
                ).findFirst()
                .ifPresent(message ->
                        message.setMessageId(insertedId)
                );
    }


    /**
     * This class is an abstract class that represent the common fields of messages view holder
     */
    private static abstract class AbstractMessageHolder extends RecyclerView.ViewHolder {
        View layout;
        TextView messageContent;
        TextView messageDate;
        TextView messageTime;

        public AbstractMessageHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.message_layout);
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


