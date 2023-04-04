package com.example.rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rental.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextView logIn;
    private FirebaseAuth mAuth;
    private EditText FullName, Email, Password, ConfirmPassword;
    private AppCompatButton signUpButton;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Find id's
        logIn = (TextView) findViewById(R.id.logINText);
        FullName = (EditText) findViewById(R.id.fullName);
        Email = (EditText) findViewById(R.id.userEmail);
        Password = (EditText) findViewById(R.id.userPass);
        ConfirmPassword = (EditText) findViewById(R.id.confirmPass);
        signUpButton = (AppCompatButton) findViewById(R.id.buttonSignup);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("SignUp", "onClick: ");
                if (FullName.getText().toString().trim().equals("")) {
                    //Show alert if left empty
                    Toast.makeText(SignUpActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    //Log.d("SignUp", "onClick: Toast Name ");
                } else if (Email.getText().toString().trim().isEmpty()) {
                    //Show alert if left empty
                    Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                } else if (Password.getText().toString().trim().isEmpty()) {
                    //Show alert if left empty
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                else if(Password.getText().toString().trim().length()<8){
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (ConfirmPassword.getText().toString().trim().isEmpty()) {
                    //Show alert if left empty
                    Toast.makeText(SignUpActivity.this, "Enter confirm password", Toast.LENGTH_SHORT).show();
                }
                else if (!Password.getText().toString().trim().equals(ConfirmPassword.getText().toString().trim())) {
                    //Show if password and confirm password doesn't match
                    Toast.makeText(SignUpActivity.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                } else {
                    //if a valid email address create a User
                    if (emailCheck(Email.getText().toString().trim())) {

                        //Log.d("SignUp", "onClick:DatabaseSave ");
                        CreateUser(
                                Email.getText().toString().trim(),
                                Password.getText().toString().trim(),
                                FullName.getText().toString().trim());
                    } else {
                        Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    boolean emailCheck(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void CreateUser(String email, String password, String userName) {
        SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
        editor.putString("email",email);
        editor.putString("pass",password);
        editor.apply();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                User user = new User(task.getResult().getUser().getUid(), userName, email, "");
                if (task.isSuccessful()) {
                    mRef.child("users").child(user.getId()).setValue(user);
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    Toast.makeText(SignUpActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Account creation FAILED!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Account creation FAILED!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}