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
import android.widget.Toast;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.dialogs.CreateApartmentDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageUserWithoutApartmentFragment#newInstance} factory
 * method to
 * create an instance of this fragment.
 */
public class HomePageUserWithoutApartmentFragment extends Fragment {

    MainActivity activity;
    View menuBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageUserWithoutApartmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageUserWithoutApartmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageUserWithoutApartmentFragment newInstance(String param1, String param2) {
        HomePageUserWithoutApartmentFragment fragment = new HomePageUserWithoutApartmentFragment();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_user_without_apartment, container, false);
        activity = (MainActivity) requireActivity();

        initMenuBtn(view);
        initCreateApartmentButton(view);
        disableBackPress();


        return view;
    }

    private void initMenuBtn(View view) {
        menuBtn = view.findViewById(R.id.menu_btn);
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
            CreateApartmentDialog createApartmentDialog = new CreateApartmentDialog(str->{
                activity.createApartment(str);
            });
            createApartmentDialog.show(getParentFragmentManager(), "createApartmentDialog");
        });
    }
}