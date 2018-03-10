package com.university.rahim.softecapp.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.university.rahim.softecapp.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ((TextView)this.findViewById(R.id.email)).setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }
}
