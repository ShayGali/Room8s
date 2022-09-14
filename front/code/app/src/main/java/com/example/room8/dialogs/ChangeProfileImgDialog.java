package com.example.room8.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.ImageFactory;
import com.example.room8.R;
import com.example.room8.adapters.ChangeProfileImgAdapter;

public class ChangeProfileImgDialog extends AppCompatDialogFragment {
    private View view;
    private final Cunsumer<Integetr> chageImgFunction;

    public ChangeProfileImgDialog(Cunsumer<Integetr> chageImgFunction){
        this.chageImgFunction= chageImgFunction;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_change_profile_img, null);

        view.findViewById(R.id.close_dialog_btn).setOnClickListener(v -> dismiss());


        RecyclerView recyclerView = view.findViewById(R.id.imgs_recyclerView);
        ChangeProfileImgAdapter adapter = new ChangeProfileImgAdapter(getLayoutInflater(), requireContext(), ImageFactory.imgs);
        recyclerView.setAdapter(adapter);
        int numOgCul = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), numOgCul, GridLayoutManager.HORIZONTAL, false));

        view.findViewById(R.id.save_change_btn).setOnClickListener(v ->{
            if(User.getInstance().getIconID() != adapter.getSelectedPosition()){

                int temp = adapter.getSelectedPosition();

                ServerRequestsService.getInstance().ChangeProfileImg(
                    adapter.getSelectedPosition(),
                    ()->{
                        User.getInstance().setIconID(temp);
                    }, 
                    ()->{
                        requireActivity().runOnUiThread(()-> chageImgFunction.accept(ImageFactory.profileImageFactory(User.getInstance().getIconID())));
                    }
                );

            chageImgFunction.accept(ImageFactory.profileImageFactory(temp));
            }
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.background);
        return dialog;
    }

}