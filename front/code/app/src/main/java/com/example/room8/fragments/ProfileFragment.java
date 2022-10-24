package com.example.room8.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.room8.ImageFactory;
import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.database.SharedPreferenceHandler;
import com.example.room8.dialogs.ChangePasswordDialog;
import com.example.room8.dialogs.ChangeProfileImgDialog;
import com.example.room8.dialogs.RoommatesDialog;
import com.example.room8.model.User;

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
        View logOut = view.findViewById(R.id.log_out_btn);
        View leaveRoomBtn = view.findViewById(R.id.leave_room_btn);
        View deleteUserDtn = view.findViewById(R.id.delete_user_btn);
        ImageView profileImage = view.findViewById(R.id.profile_img);

        if (User.getInstance().getUserName() != null)
            ((TextView) view.findViewById(R.id.user_username_textView)).setText(User.getInstance().getUserName());
        else
            view.findViewById(R.id.user_username_textView).setVisibility(View.GONE);

        if (User.getInstance().getEmail() != null)
            ((TextView) view.findViewById(R.id.user_email_textView)).setText(User.getInstance().getEmail());
        else
            view.findViewById(R.id.user_email_textView).setVisibility(View.GONE);

        profileImage.setImageResource(ImageFactory.profileImageFactory(User.getInstance().getProfileIconId()));

        view.findViewById(R.id.edit_profile_img_btn).setOnClickListener(v -> new ChangeProfileImgDialog(profileImage::setImageResource, activity).show(getParentFragmentManager(), "tag"));
        changePasswordBtn.setOnClickListener(v -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.show(getParentFragmentManager(), "Change Password");
        });

        logOut.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE)
                    activity.logout(R.id.action_profileFragment_to_loginFragment);
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);

            builder
                    .setMessage("Are you sure you what to logout?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.color.background);
            dialog.show();
        });


        if (SharedPreferenceHandler.getInstance().isInApartment()) {

            openRoom8sDialogBtn.setOnClickListener(v -> {
                RoommatesDialog dialog = new RoommatesDialog(id -> ServerRequestsService.getInstance().removeRoom8(id));
                dialog.show(getParentFragmentManager(), "Room8s");
            });

            leaveRoomBtn.setOnClickListener(v -> {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE)
                        activity.leaveApartment();
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);

                builder.setMessage("Are you sure you what to leave your apartment?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(R.color.background);
                dialog.show();
            });
        } else {
            openRoom8sDialogBtn.setVisibility(View.GONE);
            leaveRoomBtn.setVisibility(View.GONE);
        }

        deleteUserDtn.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE)
                    ServerRequestsService.getInstance().deleteUser(() -> requireActivity().runOnUiThread(() ->
                            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
                    ));
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);

            builder.setMessage("Are you sure you what to delete your user?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.color.background);
            dialog.show();
        });

        return view;
    }

}