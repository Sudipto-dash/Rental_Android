package com.example.rental.adapter;

import static com.example.rental.Methods.getFormattedTime;
import static com.example.rental.fragments.HomeFragment.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental.R;
import com.example.rental.databinding.CommentLayoutBinding;
import com.example.rental.dialogs.WarningDialog;
import com.example.rental.model.Comment;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    ArrayList<Comment> comments;
    String postId;

    public CommentAdapter(Context context, ArrayList<Comment> comments, String postId) {
        this.context = context;
        this.comments = comments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.commentTv.setText(comments.get(position).getComment());
        holder.binding.usernameTv.setText(comments.get(position).getName());
        if(!comments.get(position).getImage().equals("")){
            Picasso.get().load(comments.get(position).getImage()).into(holder.binding.imageIv);
        }else {
            holder.binding.imageIv.setImageResource(R.drawable.ic_account);
        }
        holder.binding.dateTv.setText(getFormattedTime("hh:mm a", comments.get(position).getTimeStamp()));

        if(comments.get(position).getUserId().equals(user.getId())){
            holder.binding.deleteCmntIv.setVisibility(View.VISIBLE);
        }else{
            holder.binding.deleteCmntIv.setVisibility(View.GONE);
        }

        holder.binding.deleteCmntIv.setOnClickListener(view -> {
            WarningDialog dialog = new WarningDialog(postId,comments.get(position).getId(),2);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), dialog.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CommentLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentLayoutBinding.bind(itemView);
        }
    }
}
