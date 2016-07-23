package com.testing.contactpicker;

import android.content.Context;
import android.content.SharedPreferences;

import com.testing.contactpicker.lockscreen.LockScreen;

import java.util.concurrent.locks.Lock;

/**
 * Created by Glenwin18 on 7/11/2016.
 */
public class Preferences {
    private static SharedPreferences prefs;
    private static final String storage = "configs";

    public static void setFragmentMap(Context context, String value){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fragment", value);
        editor.apply();
    }
    public static String getFragmentMap(Context context){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        return prefs.getString("fragment", "");
    }
    public static void setPrimaryContactNumber(Context context, String value){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("primaryContact", value);
        editor.apply();
    }
    public static String getPrimaryContactNumber(Context context){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        return prefs.getString("primaryContact", "");
    }
    public static void setSecondaryContactNumber(Context context, String value){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("secondaryContact", value);
        editor.apply();
    }
    public static String getSecondaryContactNumber(Context context){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        return prefs.getString("secondaryContact", "");
    }
    public static void setLock(Context context,boolean value){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LockScreen.ISLOCK, value);
        editor.apply();
    }
    public static boolean ifLock(Context context){
        prefs = context.getSharedPreferences(storage, Context.MODE_PRIVATE);
        return prefs.getBoolean(LockScreen.ISLOCK,false);
    }

}
