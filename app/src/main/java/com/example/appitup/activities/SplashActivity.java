package com.example.appitup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ativity);
    }

    @Override
    protected void onStart() {
        if (Prefs.isUserLoggedIn(SplashActivity.this)) {
            takeAction();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignIn.class));
                }
            }, 2000);
        }
        super.onStart();
    }

    private void takeAction() {
        User user = Prefs.getUser(SplashActivity.this);
        Query query;
        if (user.getUserType() == Helper.USER_STUDENT)
            query = FirebaseDatabase.getInstance().getReference("StudentUsers");
        else query = FirebaseDatabase.getInstance().getReference("AdminUsers");

        query.orderByChild("username").equalTo(user.getUsername())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user = getUserModelFromDS(ds);
                                if (user.isBlocked()) {
                                    Helper.toast(SplashActivity.this, "You have been blocked by your college admin.");
                                    Helper.signOutUser(SplashActivity.this, true);
                                } else {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private User getUserModelFromDS(DataSnapshot ds) {
        User user = new User((String) ds.child("username").getValue(), (String) ds.child("email").getValue(),
                (String) ds.child("displayName").getValue(), (String) ds.child("profileUri").getValue(), (String) ds.child("uid").getValue());
        if (ds.hasChild("isBlocked") && ds.child("isBlocked").getValue().equals(true))
            user.setBlocked(true);
        return user;
    }
}