package com.example.rental.fragments;

import static android.app.Activity.RESULT_OK;

import static com.example.rental.fragments.HomeFragment.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rental.LoginActivity;
import com.example.rental.R;
import com.example.rental.databinding.FragmentProfileBinding;
import com.example.rental.dialogs.LoadingDialog;
import com.example.rental.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private DatabaseReference dref, refer;
    private int PickImage = 1;
    private Uri uri;
    private LoadingDialog loadingDialog;

    private FragmentProfileBinding binding;
    private Drawable placeholder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        placeholder= ContextCompat.getDrawable(requireContext(), R.drawable.ic_account);
        // Inflate the layout for this fragment
        loadingDialog = new LoadingDialog();
        loadingDialog.setCancelable(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        dref = FirebaseDatabase.getInstance().getReference().child("users");
        dref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getImage().isEmpty()) {
                    binding.pUserName.setText(user.getName());
                    binding.pEmail.setText(user.getEmail());
                    binding.profileImage.setImageResource(R.drawable.ic_account);
                } else if (user != null) {
                    binding.pUserName.setText(user.getName());
                    binding.pEmail.setText(user.getEmail());
                    Picasso.get().load(user.getImage()).placeholder(placeholder).into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selected Picture"), PickImage);
            }
        });

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show
                loadingDialog.show(getChildFragmentManager(), loadingDialog.getTag());
                //Log.d("myDebug","I here");
                uploadImage();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickImage && resultCode == RESULT_OK && data != null) {
            //assert data != null;
            if (data.getData() != null) {
                uri = data.getData();
                binding.profileImage.setImageURI(uri);

            }
        }
    }

    private void uploadImage() {

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_images/" + UUID.randomUUID().toString());

        user.setEmail(binding.pEmail.getText().toString().trim());
        user.setName(binding.pUserName.getText().toString().trim());

        if (uri == null) {
            int res=updatePass();
            if (res==0||res==2) {
                uploadData(user,res);
            }
            return;
        }

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            user.setImage(uri.toString());
                            int res=updatePass();
                            if (res==0||res==2) {
                                uploadData(user,res);
                            }
                        }
                    }
                });
            }
        });
    }

    private int updatePass() {
        if (!binding.currentPassword.getText().toString().trim().isEmpty()) {
            if (!binding.newPassword.getText().toString().trim().isEmpty()) {
                if (binding.newPassword.getText().toString().trim().length() < 8) {
                    Toast.makeText(getContext(), "Minimum 8 character!", Toast.LENGTH_SHORT).show();
                    return 1;
                }
                if (binding.newPassword.getText().toString().trim().equals(binding.confirmNewPassword.getText().toString().trim())) {
                    if (!getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("pass", "").equals(binding.currentPassword.getText().toString().trim())) {
                        Toast.makeText(getContext(), "Current password not matched!", Toast.LENGTH_SHORT).show();
                        return 1;
                    }
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(binding.newPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()&&task.isSuccessful()) {
                                        getContext().getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString("pass", binding.newPassword.getText().toString().trim()).apply();
                                        Toast.makeText(getContext(), "Password Updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    return 2;
                } else {
                    Toast.makeText(getContext(), "New password not matched!", Toast.LENGTH_SHORT).show();
                    return 1;
                }
            } else {
                Toast.makeText(getContext(), "New password cannot be empty!", Toast.LENGTH_SHORT).show();
                return 2;
            }
        }
        return 0;
    }

    private void uploadData(User user,int res) {

        refer = FirebaseDatabase.getInstance().getReference().child("users").child(user.getId());

        refer.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //change email
                FirebaseAuth.getInstance().getCurrentUser().updateEmail(binding.pEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    loadingDialog.dismiss();
                                    if (res==2){
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(getContext(), LoginActivity.class));
                                        getActivity().finish();
                                    }
                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}