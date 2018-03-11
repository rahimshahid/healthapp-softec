package com.university.rahim.softecapp.UI;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.university.rahim.softecapp.Pojos.User;
import com.university.rahim.softecapp.R;

public class Login extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    User mUser;

    private TextInputLayout tilEmail;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmail = findViewById(R.id.til_email);
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);

        User mCurrentUser;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser != null && mFirebaseUser.isEmailVerified()){
            Intent i = new Intent(Login.this, MainHomeActivity.class);

            getLoggedInUser();
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
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
                Intent i = new Intent(Login.this, MainHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                getLoggedInUser();
                startActivity(i);
                finish();
            } else {
                Toast.makeText(Login.this, "Please verify Email", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(Login.this, "Error in signing in", Toast.LENGTH_SHORT).show();
    }

    public void register(View view) {
        startActivity(new Intent(this,Register.class));
    }

    public void resetPassword(View view) {
        if(isValidEmail(tilEmail)){
            mFirebaseAuth.sendPasswordResetEmail(mEmail.getText().toString().trim()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Login.this, "Password reset email sent! Please check email.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else
                                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT)
                                .show();

                        }
                    }
            );
        }
    }

    public boolean isValidEmail(TextInputLayout email){
        String text = email.getEditText().getText().toString().trim();

        if(text.isEmpty()){
            email.setError("Field required");
            return false;
        }else if(text.matches(Patterns.EMAIL_ADDRESS.pattern()))
            return true;

        email.setError("Invalid Email Address");
        return false;
    }

    public void getLoggedInUser() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                Log.d("Usman_Login", mUser.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
