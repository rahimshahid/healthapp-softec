package com.university.rahim.softecapp.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.university.rahim.softecapp.R;

public class Login extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser != null && mFirebaseUser.isEmailVerified()){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
    }

    public void login(View view) {
        String email = mEmail.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();

        mFirebaseAuth.signInWithEmailAndPassword(email, pass);

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            if (mFirebaseUser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            } else {
                Toast.makeText(Login.this, "Please verify Email", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void register(View view) {
        startActivity(new Intent(this,Register.class));
    }
}
