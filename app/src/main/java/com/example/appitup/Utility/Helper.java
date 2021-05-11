package com.example.appitup.Utility;

import android.content.Context;
import android.widget.Toast;

public class Helper {
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
