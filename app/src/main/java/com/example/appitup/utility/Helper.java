package com.example.appitup.utility;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appitup.Database.Prefs;
import com.example.appitup.activities.SignIn;
import com.example.appitup.models.Complaints;
import com.example.appitup.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static final String MY_PREFS_NAME = "APP_IT_UP_PREFS";
    public static final int USER_STUDENT = 1000;
    public static final int USER_ADMINISTRATOR = 2000;
    public static final int NOT_VOTED = 0;
    public static final int UPVOTED = 1;
    public static final int DOWNVOTED = -1;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String LEGACY_SERVER_KEY = "AAAAxCUTpvA:APA91bHbHFwvexObt6OZghNuqZix9foVCp0fBxA2qon8K5rn7gxZndeligB-9qHuOWDXpVO-Wu7bgoS-S9U8BbZf7uE8npyiHkPnWphOTjwkzqau4vbzUhom-xQGcHOUXEhAuNsd9dss";
    public static String PENDING = "PENDING";
    public static String IN_PROGRESS = "IN-PROGRESS";
    public static String RESOLVED = "RESOLVED";

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static Map<String, Object> getHashMap(Complaints complaint) {
        Map<String, Object> myObjectAsDict = new HashMap<>();
        Field[] allFields = Complaints.class.getDeclaredFields();
        for (Field field : allFields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Object value = null;
            try {
                value = field.get(complaint);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            myObjectAsDict.put(field.getName(), value);
        }
        return myObjectAsDict;
    }

    public static void signOutUser(Context context, boolean sendToSignIn) {
        FirebaseAuth.getInstance().signOut();
        Prefs.setUserLoggedIn(context, false);
        Prefs.setUserData(context, null);
        if (sendToSignIn) {
            Intent intent = new Intent(context, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void sendNotificationToUser(final String username, final Notification notification) {
        addNotificationToDb(username, notification);
        Query queryStudent = FirebaseDatabase.getInstance().getReference("StudentUsers");
        Query queryAdmin = FirebaseDatabase.getInstance().getReference("AdminUsers");

        JSONObject dataJson = new JSONObject();
        Gson gson = new Gson();
        String jsonString = gson.toJson(notification);
        try {
            dataJson = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject finalDataJson = dataJson;
        queryStudent.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String token = "";
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.hasChild("fcm_token"))
                                    token = ds.child("fcm_token").getValue().toString();
                            }
                            sendNotification(token, finalDataJson);
                        } else {
                            queryAdmin.orderByChild("username").equalTo(username)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String token = "";
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    if (ds.hasChild("fcm_token"))
                                                        token = ds.child("fcm_token").getValue().toString();
                                                }
                                                sendNotification(token, finalDataJson);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private static void addNotificationToDb(String username, Notification notification) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(username);
        String notificationId = reference.push().getKey();
        Map map = new HashMap();
        map.put("timeStamp", ServerValue.TIMESTAMP);
        notification.setTimeStampMap(map);

        reference.child(notificationId).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("addNotificationToDb", "onComplete: success");
                } else
                    Log.i("addNotificationToDb", "Comment Error : " + task.getException().getMessage());
            }
        });
    }

    public static void sendNotification(final String regToken, final JSONObject dataJson) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("data", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + Helper.LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.i("TAG", "doInBackground: " + finalResponse);
                } catch (Exception e) {
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }
}
