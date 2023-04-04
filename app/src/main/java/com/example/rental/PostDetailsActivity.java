package com.example.rental;

import static com.example.rental.Methods.getFormattedTime;
import static com.example.rental.fragments.HomeFragment.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.rental.adapter.CommentAdapter;
import com.example.rental.adapter.ImageAdapter2;
import com.example.rental.databinding.ActivityPostDetailsBinding;
import com.example.rental.dialogs.LoadingDialog;
import com.example.rental.model.Comment;
import com.example.rental.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class PostDetailsActivity extends AppCompatActivity {

    private ActivityPostDetailsBinding binding;
    private String key;
    private DatabaseReference ref;
    private LoadingDialog loadingDialog;

    private ArrayList<Comment> comments;
    private CommentAdapter adapter;

    private ArrayList<String> images;
    private ImageAdapter2 imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //call Button
        binding.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + binding.showContact.getText().toString()));
            startActivity(intent);
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        key = getIntent().getStringExtra("key");

        comments = new ArrayList<>();
        images = new ArrayList<>();
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), loadingDialog.getTag());

        adapter = new CommentAdapter(this, comments, key);
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRv.setAdapter(adapter);

        imgAdapter = new ImageAdapter2(images, this);
        binding.imageRv.setLayoutManager(new GridLayoutManager(this, 2));
        binding.imageRv.setAdapter(imgAdapter);

        ref = FirebaseDatabase.getInstance().getReference("post");
        loadPost(key);
        binding.buttonComment.setOnClickListener(v -> {
            if (binding.commentBox.getText().toString().trim().isEmpty()) {
                return;
            }
            loadingDialog.show(getSupportFragmentManager(), loadingDialog.getTag());
            String id = UUID.randomUUID().toString();
            ref.child(key).child("comments").child(id).setValue(
                    new Comment(
                            user.getId(),
                            id,
                            user.getName(),
                            user.getImage(),
                            binding.commentBox.getText().toString(),
                            System.currentTimeMillis()
                    )
            ).addOnCompleteListener(t -> {
                loadingDialog.dismiss();
                binding.commentBox.setText("");
            });
        });
    }

    private void loadPost(String key) {
        ref.child(key).get().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {

                Post post = t.getResult().getValue(Post.class);
                binding.userName.setText(post.getUser().getName());
                if (post.getUser().getImage() == null || post.getUser().getImage().isEmpty()) {
                    binding.profileImage.setImageResource(R.drawable.ic_account);
                } else {
                    Picasso.get().load(post.getUser().getImage()).into(binding.profileImage);
                }
                images.addAll(post.getImages());
                imgAdapter.notifyDataSetChanged();
                binding.time.setText(getFormattedTime("dd/MM/yy", post.getTimeStamp()));
                binding.ShowTitle.setText(post.getTitle());
                binding.showAddrss.setText(post.getAddress());
                binding.ShowDesc.setText(post.getDescription());
                binding.showContact.setText(post.getContact());
                binding.showRent.setText(post.getRent());
                String categories = "";

                if (post.isStudent()) {
                    categories += "Student, ";
                }

                if (post.isOffice()) {
                    categories += "Office, ";
                }

                if (post.isFamily()) {
                    categories += "Family, ";
                }

                binding.showCategory.setText(categories.substring(0, categories.length() - 2));

                loadingDialog.dismiss();
                loadComments(key);
            }
        });
    }

    private void loadComments(String key) {
        ref.child(key).child("comments").orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Comment comment = ds.getValue(Comment.class);
                        comments.add(comment);
                    }
                    Collections.reverse(comments);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}