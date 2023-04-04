package com.example.rental.fragments;

import static com.example.rental.fragments.HomeFragment.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rental.R;
import com.example.rental.adapter.MyPostAdapter;
import com.example.rental.databinding.FragmentMyPostsBinding;
import com.example.rental.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyPostsFragment extends Fragment {


    private FragmentMyPostsBinding binding;
    private ArrayList<Post> posts;
    private MyPostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(getLayoutInflater());

        posts = new ArrayList<>();
        adapter = new MyPostAdapter(posts);

        binding.recyclerPost.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerPost.setAdapter(adapter);

        loadMyPosts();


        return binding.getRoot();
    }

    private void loadMyPosts() {
        FirebaseDatabase.getInstance().getReference("post").orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post.getUser().getEmail().equals(user.getEmail())) {
                        posts.add(post);
                    }
                }
                if (posts.size() > 0)
                    binding.noItemTv.setVisibility(View.GONE);
                Collections.reverse(posts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}