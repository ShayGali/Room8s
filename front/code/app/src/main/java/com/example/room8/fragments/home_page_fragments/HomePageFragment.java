package com.example.room8.fragments.home_page_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.database.ServerRequestsService;
import com.example.room8.database.SharedPreferenceHandler;
import com.example.room8.model.Apartment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;

public class HomePageFragment extends Fragment {

    View view;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServerRequestsService.getInstance().getUserData();
        ServerRequestsService.getInstance().getRoom8s();
        ServerRequestsService.getInstance().getApartmentData();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });
        view = inflater.inflate(R.layout.fragment_home_page, container, false);


        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        View profileBtn = view.findViewById(R.id.go_to_profile_btn);
        View tasksBtn = view.findViewById(R.id.go_to_tasks_btn);
        View messagesBtn = view.findViewById(R.id.go_to_messages_btn);
        View walletBtn = view.findViewById(R.id.go_to_wallet_btn);
        View menuBtn = view.findViewById(R.id.menu_btn);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        this.refreshData();

        menuBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.home_page_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.settings:
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_profileFragment);
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
        profileBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_profileFragment));

        tasksBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_tasksFragment));

        messagesBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_message_Fragment));
        walletBtn.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_walletFragment));

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void refreshData() {
        ServerRequestsService.getInstance().getApartmentId(() -> {
            ServerRequestsService.getInstance().getUserData();
            if (SharedPreferenceHandler.getInstance().isInApartment()) {
                ServerRequestsService.getInstance().getAllTask(null);
                ServerRequestsService.getInstance().getRoom8s();
                ServerRequestsService.getInstance().getExpenses();
                ServerRequestsService.getInstance().getApartmentData();

            } else {
                try {
                Navigation.findNavController(view).navigate(R.id.action_homePageUserWithoutApartmentFragment_to_profileFragment);
                }catch (IllegalArgumentException ignored){}
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
