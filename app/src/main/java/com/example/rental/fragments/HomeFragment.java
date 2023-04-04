package com.example.rental.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rental.CategoricalViewActivity;
import com.example.rental.CreateOrEditPostActivity;
import com.example.rental.R;
import com.example.rental.SearchResultActivity;
import com.example.rental.adapter.PostAdapter;
import com.example.rental.databinding.FragmentHomeBinding;
import com.example.rental.dialogs.LoadingDialog;
import com.example.rental.model.Post;
import com.example.rental.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {


    private DatabaseReference dref;
    private Activity createPost;
    private ArrayList<Post> posts;
    private FragmentHomeBinding binding;
    private PostAdapter adapter;
    private LoadingDialog loadingDialog;
    public static User user;
    private Drawable ic_account_circle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        createPost = getActivity();
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        posts = new ArrayList<>();
        adapter = new PostAdapter(posts);
        ic_account_circle = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account);
        binding.recyclerPost.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerPost.setAdapter(adapter);

        initListeners();
        loadPosts();

        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);
        //show
        loadingDialog.show(getChildFragmentManager(), loadingDialog.getTag());

        binding.search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(binding.search.getText().toString().trim().length()==0){
                    Toast.makeText(createPost, "Please enter something", Toast.LENGTH_SHORT).show();
                    binding.search.requestFocus();
                    return true;
                }
                Intent intent= new Intent(createPost, SearchResultActivity.class);
                intent.putExtra("searchTerm",binding.search.getText().toString());
                startActivity(intent);
                return true;
            }
        });

        return binding.getRoot();
    }

    private void initListeners() {
        binding.studentCatBt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoricalViewActivity.class);
            intent.putExtra("category", "student");
            startActivity(intent);
        });
        binding.familyCatBt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoricalViewActivity.class);
            intent.putExtra("category", "family");
            startActivity(intent);
        });
        binding.officeCatBt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoricalViewActivity.class);
            intent.putExtra("category", "office");
            startActivity(intent);
        });
    }

    private void loadPosts() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("post");
        dref.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    posts.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Post post = ds.getValue(Post.class);
                        posts.add(post);
                    }
                    Collections.reverse(posts);
                    adapter.notifyDataSetChanged();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onStart() {
        super.onStart();
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(createPost, CreateOrEditPostActivity.class));
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dref = FirebaseDatabase.getInstance().getReference().child("users");
        dref.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null && user.getImage().isEmpty()) {
                    binding.userName.setText(user.getName());
                    binding.profileImage.setImageResource(R.drawable.ic_account);
                } else if (user != null) {
                    binding.userName.setText(user.getName());
                    Picasso.get().load(user.getImage()).placeholder(ic_account_circle).into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}