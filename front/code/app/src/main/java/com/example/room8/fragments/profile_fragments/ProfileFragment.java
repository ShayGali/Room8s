package com.example.room8.fragments.profile_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.dialogs.ChangePasswordDialog;
import com.example.room8.dialogs.RoommatesDialog;
import com.example.room8.model.Apartment;

public class ProfileFragment extends Fragment {

    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        activity = (MainActivity) requireActivity();


        View changePasswordBtn = view.findViewById(R.id.change_password_btn);
        View openRoom8sDialogBtn = view.findViewById(R.id.rooms_dialog_btn);
        View leaveRoomBtn = view.findViewById(R.id.leave_room_btn);


        changePasswordBtn.setOnClickListener(v -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(password -> activity.changePassword(password));
            dialog.show(getParentFragmentManager(), "Change Password");
        });

        openRoom8sDialogBtn.setOnClickListener(v -> {
            RoommatesDialog dialog = new RoommatesDialog(Apartment.getInstance().getRoommates(), id -> activity.removeRoom8s(id));
            dialog.show(getParentFragmentManager(), "Room8s");
        });

        leaveRoomBtn.setOnClickListener(v -> {
            activity.leaveApartment();
        });

        return view;
    }
}