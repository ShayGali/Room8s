package com.example.room8.fragments.home_page_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.database.SharedPreferenceHandler;
import com.example.room8.model.Apartment;

import java.lang.ref.WeakReference;

public class HomePageFragment extends Fragment {

    View menuBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).fetchUserData();
        ((MainActivity) requireActivity()).fetchRoom8();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);


        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        View profileBtn = view.findViewById(R.id.go_to_profile_btn);
        View tasksBtn = view.findViewById(R.id.go_to_tasks_btn);
        View messagesBtn = view.findViewById(R.id.go_to_messages_btn);
        View walletBtn = view.findViewById(R.id.go_to_wallet_btn);
        menuBtn = view.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.home_page_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.settings:
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_profileFragment);
                        return true;
                    case R.id.logout:
                        ((MainActivity) requireActivity()).logout(R.id.action_homePageFragment_to_loginFragment);
                        return true;
                }
                return false;
            });
            popupMenu.show();
        });

        // navigate to other fragments
        profileBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_profileFragment));

        tasksBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_tasksFragment));

        messagesBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_message_Fragment));
        walletBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_walletFragment));
        return view;
    }
}
