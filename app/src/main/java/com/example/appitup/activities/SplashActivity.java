package com.example.appitup.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            showBackOnlineUI();
            isConnected = true;
            takeAction();
        }

        @Override
        public void onLost(Network network) {
            showNoInternetUI();
            isConnected = false;
        }
    };

    private void showBackOnlineUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "Back Online", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    private void showNoInternetUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.black))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        } else {
            takeAction();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ativity);
        parentLayout = findViewById(android.R.id.content);

        if (!Helper.isInternetAvailable(this)) {
            showNoInternetUI();
        } else {
            takeAction();
        }
    }

    private void takeAction() {
        if (Prefs.isUserLoggedIn(SplashActivity.this)) {
            checkIsBlocked();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignIn.class));
                }
            }, 2000);
        }
    }

    private void checkIsBlocked() {
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