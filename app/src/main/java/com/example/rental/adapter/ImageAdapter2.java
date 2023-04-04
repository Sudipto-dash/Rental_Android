package com.example.rental.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental.R;
import com.example.rental.databinding.ImageLayout2Binding;
import com.example.rental.databinding.ImageLayoutBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter2 extends RecyclerView.Adapter<ImageAdapter2.ViewHolder> {

    ArrayList<String> images;
    Context context;

    public ImageAdapter2(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_layout_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(images.get(position)).into(holder.binding.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageLayout2Binding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLayout2Binding.bind(itemView);
        }
    }
}
