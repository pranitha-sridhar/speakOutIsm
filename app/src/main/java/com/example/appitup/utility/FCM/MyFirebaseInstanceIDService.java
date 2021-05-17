package com.example.appitup.utility.FCM;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.appitup.Database.Prefs;
import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
//import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //saveTokenToServer(refreshedToken);


    private void saveTokenToServer(String token) {
        User user = Prefs.getUser(this);
        DatabaseReference databaseReference;
        if (user.getUserType() == Helper.USER_STUDENT)
            databaseReference = FirebaseDatabase.getInstance().getReference("StudentUsers");
        else databaseReference = FirebaseDatabase.getInstance().getReference("AdminUsers");

        databaseReference.child(user.getUid()).child("fcm_token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // successfully saved
                    Prefs.getUser(getApplicationContext()).setFcm_token(token);
                } else {
                    // error
                }
            }
        });
    }
}
