package com.tooroq.myapplock;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private static final String PREF = "myapplock_prefs";
    private static final String KEY_PIN = "pin";
    private static final String KEY_LOCKED = "locked_apps";

    public static void savePin(Context c, String pin){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY_PIN, pin).apply();
    }
    public static String getPin(Context c){
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_PIN, "1234"); // افتراضي 1234
    }

    public static void setLockedApps(Context c, Set<String> set){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putStringSet(KEY_LOCKED, set).apply();
    }
    public static Set<String> getLockedApps(Context c){
        Set<String> s = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getStringSet(KEY_LOCKED, new HashSet<String>());
        return new HashSet<>(s); // clone to avoid concurrent modification
    }
}
