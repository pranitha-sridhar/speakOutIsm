package com.example.appitup.utility;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.appitup.Database.Prefs;
import com.example.appitup.activities.SignIn;
import com.example.appitup.models.Complaints;
import com.google.firebase.auth.FirebaseAuth;

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
}
