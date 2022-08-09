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
import com.example.room8.model.Apartment;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    View menuBtn;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // if (((MainActivity) requireActivity()).checkIfJwtTokenExists()) {
        // Navigation.findNavController(requireActivity(),
        // R.id.main_nav_host_fragment).navigate(R.id.action_homePageFragment_to_loginFragment);
        // }

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
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_settingsFragment);
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
        profileBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_profileFragment);
        });

        tasksBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_tasksFragment);
        });

        messagesBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_message_Fragment);
        });

        WeakReference<TextView> apartmentNameTextView, apartmentNumTextView, numberOfRoommatesTextview;
        apartmentNameTextView = new WeakReference<>(view.findViewById(R.id.apartment_name_textView));
        apartmentNumTextView = new WeakReference<>(view.findViewById(R.id.apartment_num_textView));
        numberOfRoommatesTextview = new WeakReference<>(view.findViewById(R.id.number_of_roommates_textView));
        activity.fetchApartmentData(apartmentNameTextView, apartmentNumTextView, numberOfRoommatesTextview);

        // TODO: get expenses data -
        // TODO: get tasks data - next task
        walletBtn.setOnClickListener(v -> {
            // Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_walletFragment);
        });
        return view;
    }
}