package com.example.room8.fragments.message_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.room8.R;
import com.example.room8.adapters.ChatAdapter;
import com.example.room8.database.ChatHandler;
import com.example.room8.model.Apartment;
import com.example.room8.model.Message;
import com.example.room8.model.User;

import org.json.JSONException;

import java.text.ParseException;
import java.util.Date;

public class ChatFragment extends Fragment {

    View view;
    EditText msgInput;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    ChatHandler chatHandler;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false);

        msgInput = view.findViewById(R.id.enter_message_EditText);

        recyclerView = view.findViewById(R.id.messages_RecyclerView);
        chatAdapter = new ChatAdapter(getLayoutInflater());
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        chatHandler = new ChatHandler(jsonObject -> requireActivity().runOnUiThread(() -> {
            try {
                if (jsonObject.has("insertId")) { // if we get back the message id from the data base
                    chatAdapter.setMessageIdByUUID(jsonObject.getInt("insertId"), jsonObject.getString("messageUUID"));
                } else {
                    chatAdapter.addMessage(jsonObject);
                }
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            } catch (ParseException | JSONException e) {
                e.printStackTrace();
            }
        }));

        initSendBtn();
        initHeader();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatHandler.closeConnection();
    }

    void initSendBtn() {
        User user = User.getInstance();


        view.findViewById(R.id.send_btn).setOnClickListener(v -> {
            Message message = new Message(user.getUserName(), msgInput.getText().toString(), new Date(), user.getProfileIconId(), true);
            chatHandler.sendMsg(message);
            chatAdapter.addMessage(message);
            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            resetMessageEdit();
        });
    }

    private void resetMessageEdit() {
        msgInput.setText("");
    }

    void initHeader() {
        ((TextView) view.findViewById(R.id.apartment_name_TextView)).setText(Apartment.getInstance().getName());
    }
}