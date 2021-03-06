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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //TODO: check if the user is already login

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        View loginButton = view.findViewById(R.id.login_btn);
        View forgetPasswordButton = view.findViewById(R.id.go_to_forgot_password_btn);
        View registerButton = view.findViewById(R.id.go_to_register_btn);

        EditText emailInput = view.findViewById(R.id.login_email_EditText);
        EditText passwordInput = view.findViewById(R.id.login_password_EditText);


        loginButton.setOnClickListener(v -> {
            activity.login(new WeakReference<>(emailInput), new WeakReference<>(passwordInput));
        });


        registerButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment)
        );

        forgetPasswordButton.setOnClickListener(v -> {
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgetPasswordFragment);
                }
        );
        return view;
    }
}