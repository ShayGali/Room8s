package com.example.room8.fragments.message_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.adapters.MessagesAdapter;
import com.example.room8.database.ChatHandler;

public class MessageFragment extends Fragment {

    View menuBtn;
    MessagesAdapter messageAdapter;


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.messages_RecyclerView);
        messageAdapter = new MessagesAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // TODO: change the name to the user name
        ChatHandler chatHandler = new ChatHandler(((MainActivity) getActivity()), view.findViewById(R.id.enter_message_EditText), view.findViewById(R.id.send_btn), recyclerView, messageAdapter);
        chatHandler.initializeSocketConnection();

        menuBtn = view.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.home_page_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.settings:
                        Toast.makeText(getContext(), "item1", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        Toast.makeText(getContext(), "item2", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            });
            popupMenu.show();
        });

        return view;
    }

}