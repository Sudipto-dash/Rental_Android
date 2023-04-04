package com.example.rental.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental.CreateOrEditPostActivity;
import com.example.rental.EditPostActivity;
import com.example.rental.PostDetailsActivity;
import com.example.rental.R;
import com.example.rental.databinding.MypostsLayoutBinding;
import com.example.rental.databinding.PostLayoutBinding;
import com.example.rental.dialogs.WarningDialog;
import com.example.rental.model.Post;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.PostViewHolder> {

    ArrayList<Post> posts;
    Context context;

    public MyPostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.myposts_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.binding.titleTv.setText(posts.get(position).getTitle());
        holder.binding.descriptionTv.setText(posts.get(position).getDescription());
        holder.binding.rentTv.setText(posts.get(position).getRent());
        Picasso.get().load(posts.get(position).getImages().get(0)).into(holder.binding.imageIv);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("key", posts.get(position).getKey());
            context.startActivity(intent);
        });

        holder.binding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateOrEditPostActivity.class);
            intent.putExtra("postId", posts.get(position).getKey());
            context.startActivity(intent);
        });

        holder.binding.deletButton.setOnClickListener(view -> {
            WarningDialog dialog = new WarningDialog(posts.get(position).getKey(),"",1);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), dialog.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        MypostsLayoutBinding binding;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MypostsLayoutBinding.bind(itemView);
        }
    }
}
