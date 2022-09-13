package com.example.room8.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;

public class ChangeProfileImgAdapter extends RecyclerView.Adapter<ChangeProfileImgAdapter.ProfileImgHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final int[] imgs;

    public ChangeProfileImgAdapter(LayoutInflater inflater, Context context, int[] imgs) {
        this.inflater = inflater;
        this.context = context;
        this.imgs = imgs;
    }

    @NonNull
    @Override
    public ProfileImgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileImgHolder(inflater.inflate(R.layout.view_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileImgHolder holder, int position) {
        holder.imageView.setImageResource(imgs[position]);
        holder.imageView.setOnClickListener(v -> {
            holder.layout.setBackground(ContextCompat.getDrawable(context, R.drawable.style_layout_border_gray));
        });
    }

    @Override
    public int getItemCount() {
        return imgs.length;
    }

    protected static class ProfileImgHolder extends RecyclerView.ViewHolder {
        ConstraintLayout  layout;
        ImageView imageView;


        public ProfileImgHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
