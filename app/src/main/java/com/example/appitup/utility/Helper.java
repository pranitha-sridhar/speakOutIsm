package com.example.appitup.utility;

import android.content.Context;
import android.widget.Toast;

public class Helper {
    public static final String MY_PREFS_NAME = "APP_IT_UP_PREFS";
    public static final int USER_STUDENT = 1000;
    public static final int USER_ADMINISTRATOR = 2000;
    public static String PENDING = "PENDING";
    public static String IN_PROGRESS = "IN-PROGRESS";
    public static String RESOLVED = "RESOLVED";

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
