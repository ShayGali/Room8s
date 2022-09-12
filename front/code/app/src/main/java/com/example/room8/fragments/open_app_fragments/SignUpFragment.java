package com.example.room8.fragments.open_app_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.room8.MainActivity;
import com.example.room8.R;

import java.lang.ref.WeakReference;

public class SignUpFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        View submitBtn = view.findViewById(R.id.submit_register_btn);
        View goToLoginBtn = view.findViewById(R.id.go_to_login_from_sginup_btn);

        EditText usernameEditText = view.findViewById(R.id.register_username_EditText);
        EditText emailEditText = view.findViewById(R.id.register_email_EditText);
        EditText passwordEditText = view.findViewById(R.id.register_password_EditText);

        submitBtn.setOnClickListener(v -> {
                    MainActivity activity = (MainActivity) requireActivity();
                    activity.register(usernameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
        );

        goToLoginBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment)
        );


        return view;
    }
}