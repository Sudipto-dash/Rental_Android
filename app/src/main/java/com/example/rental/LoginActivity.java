package com.example.rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView signUp;
    private EditText Useremail,UserPassword;
    private FirebaseAuth mAuth;
    private TextView forgotPass;
    private AppCompatButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = (TextView)findViewById(R.id.signUpText);
        Useremail = (EditText)findViewById(R.id.userEmail);
        UserPassword = (EditText)findViewById(R.id.userPass);
        loginButton = (AppCompatButton)findViewById(R.id.buttonLogin);
        forgotPass = (TextView) findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Useremail.getText().toString().trim().isEmpty() && emailCheck(Useremail.getText().toString().trim())  ){
                    if(!UserPassword.getText().toString().trim().isEmpty()){
                        loginUser(Useremail.getText().toString().trim(),UserPassword.getText().toString().trim());
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Invalid password",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,"Invalid credentials!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Reset password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Useremail.getText().toString().trim().isEmpty() && emailCheck(Useremail.getText().toString().trim())) {
                    String email = Useremail.getText().toString().trim();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LoginActivity.this, "Reset email Sent.Check your email.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Enter you email first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //Function Calls
    boolean emailCheck(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    void loginUser(String email,String pass){
        SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
        editor.putString("email",email);
        editor.putString("pass",pass);
        editor.apply();
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this,"Invalid!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"Login failed !!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            Toast.makeText(LoginActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();

        }
    }



    //End of login activity
}