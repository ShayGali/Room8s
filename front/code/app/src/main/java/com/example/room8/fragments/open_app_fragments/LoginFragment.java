package com.example.room8.fragments.open_app_fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.room8.MainActivity;
import com.example.room8.R;

import java.lang.ref.WeakReference;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        View loginButton = view.findViewById(R.id.login_btn);
        View forgetPasswordButton = view.findViewById(R.id.go_to_forgot_password_btn);
        View registerButton = view.findViewById(R.id.go_to_register_btn);

        EditText emailInput = view.findViewById(R.id.login_email_EditText);
        EditText passwordInput = view.findViewById(R.id.login_password_EditText);
        TextView errorMsg = view.findViewById(R.id.error_msg_login);

        loginButton.setOnClickListener(v -> activity.login(emailInput.getText().toString(), passwordInput.getText().toString(), new WeakReference<>(errorMsg)));


        registerButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment)
        );

        forgetPasswordButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        );
        return view;
    }
}
