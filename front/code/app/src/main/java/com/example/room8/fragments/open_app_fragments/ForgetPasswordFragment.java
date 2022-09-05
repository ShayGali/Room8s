package com.example.room8.fragments.open_app_fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.room8.R;

public class ForgetPasswordFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_forgot_password, container, false);

        EditText emailInput = view.findViewById(R.id.email_input_forgot_password);

        view.findViewById(R.id.submit_btn_frogot_password).setOnClickListener(v->{
            String email = emailInput.getText().toString();
            ServerRequestsService.getInstance().forgotPassword(email);
        });


        //TODO: create this button
        view.findViewById(R.id.go_to_login_btn).setOnClickListener(v->
            Navigation.findNavController(view).navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
        );

        return view;
    }
}