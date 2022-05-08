package com.example.room8.fragments.message_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.room8.R;
import com.example.room8.adapters.MessagesAdapter;
import com.example.room8.model.Message;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Message_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Message_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    MessagesAdapter messageAdapter;

    public Message_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment message_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Message_Fragment newInstance(String param1, String param2) {
        Message_Fragment fragment = new Message_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.messages_RecyclerView);
        messageAdapter = new MessagesAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        addMessages();
        return view;
    }

    private void addMessages() {

        messageAdapter.addMessage(new Message("Yossi", "oisafofsjiosfasfo", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Yossi", "aw3reasfafasdf", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Davud", "Continual delighted as elsewhere am convinced unfeeling. Introduced stimulated attachment no by projection. ", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Yossi", "oisafofsjiosfasfo", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Davud", "oisafofsjiosfasfo", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Davud", "In friendship diminution instrument so. Son sure paid door with say them. Two among sir sorry men court. Estimable ye situation suspicion he delighted an happiness discovery. Fact are size cold why had part. If believing or sweetness otherwise in we forfeited. Tolerably an unwilling arranging of determine. Beyond rather sooner so if up wishes or.", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Yossi", "hey", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Davud", "oisafofsjiosfasfo", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Yossi", "125", "Jone 10", "10:35"));
        messageAdapter.addMessage(new Message("Davud", "oisafofsjiosfasfo", "Jone 10", "10:35"));
    }
}