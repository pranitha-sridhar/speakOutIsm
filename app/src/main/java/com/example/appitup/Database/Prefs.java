package com.example.appitup.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class Prefs {
    public static void setFilter_selectedChip(Context context,int i){
        SharedPreferences.Editor editor=context.getSharedPreferences(Helper.MY_PREFS_NAME,Context.MODE_PRIVATE).edit();
        editor.putInt("stateOfChip",i);
        editor.apply();
    }

    public  static int getFilter_selectedChip(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Helper.MY_PREFS_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt("stateOfChip",-1);
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return (sharedPreferences.getBoolean("isUserLoggedIn", false)
                && Prefs.getUser(context) != null && Prefs.getUser(context).getUsername() != null
                && FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    public static void setUserData(Context context, User user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
    }

    public static void setUserLoggedIn(Context context, boolean b) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isUserLoggedIn", b);
        editor.apply();

        if (!b) {
            editor.clear();
            editor.apply();
        }
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        return gson.fromJson(json, User.class);
    }

    public static void setFirstUse(Context context, boolean bool) {
        Log.i("FirstChange ", bool + "");
        SharedPreferences.Editor editor = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("first", bool);
        editor.apply();
    }

    public static boolean getFirstUse(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Helper.MY_PREFS_NAME, Context.MODE_PRIVATE);
        Log.i("GetFirstChange ", sharedPreferences.getBoolean("first", true) + "");
        return sharedPreferences.getBoolean("first", true);
    }
}
