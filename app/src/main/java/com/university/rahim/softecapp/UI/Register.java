package com.university.rahim.softecapp.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.university.rahim.softecapp.Pojos.Points;
import com.university.rahim.softecapp.Pojos.User;
import com.university.rahim.softecapp.R;

public class Register extends AppCompatActivity {

    private Button mRegisterButton;
    private TextInputLayout mName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirmPassword;
    private TextInputLayout mAge;
    private TextInputLayout mHeight;
    private TextInputLayout mWeight;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public boolean isValidEmail(TextInputLayout email){
        String text = email.getEditText().getText().toString();

        if(text.isEmpty()){
            email.setError("Field required");
            return false;
        }else if(text.matches(Patterns.EMAIL_ADDRESS.pattern()))
            return true;

        email.setError("Invalid Email Address");
        return false;
    }

    private boolean areValidPasswords(TextInputLayout tilPassword, TextInputLayout tilConfirmPassword) {
        String password = tilPassword.getEditText().getText().toString();
        String confirmPassword = tilConfirmPassword.getEditText().getText().toString();

        boolean isValid = true;
        if (password.isEmpty()) {
            tilPassword.setError("Field required");
            isValid = false;
        }
        if (confirmPassword.isEmpty()){
            tilConfirmPassword.setError("Field required");
            isValid = false;
        }
        if (password.length() < 5) {
            tilPassword.setError("Min 5 characters required");
            isValid = false;
        }
        if (confirmPassword.length() < 5){
            tilConfirmPassword.setError("Min 5 characters required");
            isValid = false;
        }

        if(!password.equals(confirmPassword)){
            tilPassword.setError("Password don't match");
            tilConfirmPassword.setError("Password don't match");
            isValid = false;
        }

        return isValid;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName = findViewById(R.id.til_name);
        mEmail = findViewById(R.id.til_email);
        mPassword = findViewById(R.id.til_password);
        mConfirmPassword = findViewById(R.id.til_confirm_password);

        mAge = findViewById(R.id.til_age);
        mHeight = findViewById(R.id.til_height);
        mWeight = findViewById(R.id.til_weight);

        mRegisterButton = findViewById(R.id.btn_register);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidInfo = true;

                if(mName.getEditText().getText().toString().isEmpty()){
                    mName.setError("Field required");
                    isValidInfo = false;
                }

                //TODO: check age, height and weight

                if (isValidEmail(mEmail) && areValidPasswords(mPassword, mConfirmPassword) && isValidInfo) {

                    final ProgressDialog dialog = new ProgressDialog(Register.this);
                    dialog.setMessage("Registering User...");
                    dialog.show();

                    mFirebaseAuth.createUserWithEmailAndPassword(mEmail.getEditText().getText().toString().trim(),
                            mPassword.getEditText().getText().toString().trim())
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {

                                        mFirebaseAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(Register.this, "Email sent!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Toast.makeText(Register.this, "Email not sent!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                        Toast.makeText(Register.this, "User registered! Please verify email!", Toast.LENGTH_LONG).show();

                                        User user = new User(mName.getEditText().getText().toString().trim(),
                                                Double.parseDouble(mHeight.getEditText().getText().toString().trim()),
                                                Double.parseDouble(mWeight.getEditText().getText().toString().trim()),
                                                null );

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                        ref.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user);

                                        ref = FirebaseDatabase.getInstance().getReference("Leaderboard");

                                        Points points = new Points(mFirebaseAuth.getCurrentUser().getUid(),user.getName(),0);
                                        ref.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(points);

                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Register.this, "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

    }


    public void login(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}
