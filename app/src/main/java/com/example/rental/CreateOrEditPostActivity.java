package com.example.rental;

import static com.example.rental.fragments.HomeFragment.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.rental.adapter.ImageAdapter;
import com.example.rental.databinding.ActivityCreatePostBinding;
import com.example.rental.dialogs.LoadingDialog;
import com.example.rental.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class CreateOrEditPostActivity extends AppCompatActivity {


    private ActivityCreatePostBinding binding;
    private LoadingDialog loadingDialog;
    private boolean isStudent, isFamily, isOffice;
    private String postId;

    private ArrayList<String> images;
    private ImageAdapter adapter;
    private Queue<Uri> uploadQueue;
    private ArrayList<String> uploadedImages;
    private StorageReference ref;
    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);

        ref = FirebaseStorage.getInstance().getReference().child("post_images/");
        images = new ArrayList<>();
        uploadQueue = new LinkedList<>();
        uploadedImages = new ArrayList<>();
        adapter = new ImageAdapter(images, this);

        binding.imagesRv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.imagesRv.setAdapter(adapter);

        postId = getIntent().getStringExtra("postId");

        if (postId != null && !postId.equals("")) {
            binding.textTitle.setText("Edit Post");
            binding.buttonPost.setText("Update");
            loadPost(postId);
        }


        binding.addImageTbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Selected Picture"), 1);
            }
        });

        binding.buttonPost.setOnClickListener(v -> {
            if (validator()) {
                startUploading();
            }
        });
        binding.backArrow.setOnClickListener(view -> finish());
    }


    private void loadPost(String key) {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        FirebaseDatabase.getInstance().getReference("post").child(key).get().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                post = t.getResult().getValue(Post.class);
                binding.addTitle.setText(post.getTitle());
                binding.addDesc.setText(post.getDescription());
                binding.addaAddress.setText(post.getAddress());
                binding.contact.setText(post.getContact());
                binding.rent.setText(post.getRent());
                if (post.isStudent()) {
                    isStudent = true;
                    binding.checkboxStudent.setChecked(true);
                }
                if (post.isFamily()) {
                    isFamily = true;
                    binding.checkboxFamily.setChecked(true);
                }
                if (post.isOffice()) {
                    isOffice = true;
                    binding.checkboxOffice.setChecked(true);
                }

                uploadedImages.addAll(post.getImages());
                images.addAll(post.getImages());
                adapter.notifyDataSetChanged();
            }
            loadingDialog.dismiss();
        });
    }

    private void uploadPost() {
        String key = "";
        if (postId != null && !postId.equals("")) {
            key = postId;
        } else {
            key = UUID.randomUUID().toString();
        }
        FirebaseDatabase.getInstance().getReference("post").child(key).setValue(new Post(user, key, binding.addTitle.getText().toString(), binding.addDesc.getText().toString(), binding.addaAddress.getText().toString(), binding.contact.getText().toString(), binding.rent.getText().toString(), isStudent, isFamily, isOffice, System.currentTimeMillis(), uploadedImages)).
                addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Toast.makeText(CreateOrEditPostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateOrEditPostActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }).addOnFailureListener(t -> {
                    Toast.makeText(CreateOrEditPostActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }

    private void startUploading() {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        uploadQueue = new LinkedList<>();
        for (String image : images) {
            uploadQueue.add(Uri.parse(image));
        }
        uploadImage();
    }

    private void uploadImage() {
        if (uploadQueue.isEmpty()) {
            uploadPost();
        } else {
            Uri uri = uploadQueue.peek();
            if (uri.toString().contains("https://")) {
                uploadQueue.poll();
                uploadImage();
                return;
            }
            String id = UUID.randomUUID().toString();
            ref.child(id).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.child(id).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                uploadedImages.add(task.getResult().toString());
                                uploadQueue.poll();
                                uploadImage();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    images.add(data.getClipData().getItemAt(i).getUri().toString());
                }
            } else if (data.getData() != null) {
                images.add(data.getData().toString());
            }
            adapter.notifyDataSetChanged();

            images.forEach(s -> Log.d("TAG", "onActivityResult: " + s));
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkboxStudent:
                isStudent = checked;
                break;
            case R.id.checkboxFamily:
                isFamily = checked;
                break;
            case R.id.checkboxOffice:
                isOffice = checked;
                break;
        }
    }

    private boolean validator() {
        if (images.size() == 0) {
            Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.addTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.addDesc.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.addaAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.contact.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter contact no.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.contact.getText().toString().length() != 11 && !binding.contact.getText().toString().matches("[0-9]")) {
            Toast.makeText(this, "Please enter a valid contact no.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.rent.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter rent", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.rent.getText().toString().length() > 6 && !binding.rent.getText().toString().matches("[0-9]")) {
            Toast.makeText(this, "Please enter reasonable rent", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isStudent && !isFamily && !isOffice) {
            Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}