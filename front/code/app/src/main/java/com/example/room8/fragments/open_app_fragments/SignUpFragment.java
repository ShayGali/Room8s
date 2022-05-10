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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        View submitBtn = view.findViewById(R.id.submit_register_btn);
        View goToLoginBtn = view.findViewById(R.id.go_to_login_from_sginup_btn);

        EditText usernameEditText = view.findViewById(R.id.register_username_EditText);
        EditText emailEditText = view.findViewById(R.id.register_email_EditText);
        EditText passwordEditText = view.findViewById(R.id.register_password_EditText);
        submitBtn.setOnClickListener(v -> {
                    //TODO: make register function
                    MainActivity activity = (MainActivity)getActivity();
                    String username = usernameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    activity.register(username,email,password);
//                    Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homePageFragment);
                }
        );

        goToLoginBtn.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment)
        );


        return view;
    }
}