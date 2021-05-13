package com.example.appitup.utility;

import android.content.Context;
import android.widget.Toast;

import com.example.appitup.models.Complaints;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
}
