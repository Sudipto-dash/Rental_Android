package com.example.rental;

import static com.example.rental.fragments.HomeFragment.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.rental.adapter.PostAdapter;
import com.example.rental.databinding.ActivityCategoricalViewBinding;
import com.example.rental.dialogs.LoadingDialog;
import com.example.rental.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class CategoricalViewActivity extends AppCompatActivity {

    private ActivityCategoricalViewBinding binding;
    private PostAdapter adapter;
    private ArrayList<Post> posts;
    private String category;

    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoricalViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        category = getIntent().getStringExtra("category");
        posts = new ArrayList<>();
        adapter = new PostAdapter(posts);

        binding.categoricalRecyclerPost.setLayoutManager(new LinearLayoutManager(this));
        binding.categoricalRecyclerPost.setAdapter(adapter);

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), loadingDialog.getTag());

        loadCategoricalData();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadCategoricalData() {
        FirebaseDatabase.getInstance().getReference().child("post").orderByChild("timeStamp").get().addOnSuccessListener(snapshot -> {
            posts.clear();
            for (DataSnapshot ds : snapshot.getChildren()) {
                Post post = ds.getValue(Post.class);
//                if(!post.getUser().getId().equals(user.getId()))
//                    continue;
                switch (category) {
                    case "student":
                        if (post.isStudent())
                            posts.add(post);
                        break;
                    case "family":
                        if (post.isFamily())
                            posts.add(post);
                        break;
                    case "office":
                        if (post.isOffice())
                            posts.add(post);
                        break;
                }
            }
            if (posts.size() > 0)
                binding.noItemTv.setVisibility(View.GONE);
            Collections.reverse(posts);
            loadingDialog.dismiss();
            adapter.notifyDataSetChanged();
        });
    }
}