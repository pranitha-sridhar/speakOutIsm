package com.example.appitup.utility;

import android.content.Context;
import android.widget.Toast;

public class Helper {
    public static String PENDING = "PENDING";
    public static String IN_PROGRESS = "IN-PROGRESS";
    public static String RESOLVED = "RESOLVED";

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
