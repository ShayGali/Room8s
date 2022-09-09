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

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.dialogs.CreateApartmentDialog;

public class HomePageUserWithoutApartmentFragment extends Fragment {

    MainActivity activity;


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_user_without_apartment, container, false);

        initMenuBtn(view);
        initCreateApartmentButton(view);
        disableBackPress();

        ServerRequestsService.getInstance().getJoinReq(jsonArray ->{}); // TODO

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    private void initMenuBtn(View view) {
        View menuBtn = view.findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.home_page_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.settings:
                        Navigation.findNavController(view).navigate(R.id.action_homePageUserWithoutApartmentFragment_to_settingsFragment);
                        return true;
                    case R.id.logout:
                        ((MainActivity) requireActivity()).logout(R.id.action_homePageUserWithoutApartmentFragment_to_loginFragment);
                        return true;
                }
                return false;
            });
            popupMenu.show();

        });
    }

    private void disableBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
    }

    private void initCreateApartmentButton(View view){
        View createApartmentButton = view.findViewById(R.id.create_apartment_btn);
        createApartmentButton.setOnClickListener(v->{
            CreateApartmentDialog createApartmentDialog = new CreateApartmentDialog(str-> activity.createApartment(str));
            createApartmentDialog.show(getParentFragmentManager(), "createApartmentDialog");
        });
    }

}