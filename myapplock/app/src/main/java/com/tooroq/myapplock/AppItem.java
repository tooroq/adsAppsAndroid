package com.tooroq.myapplock;


import android.graphics.drawable.Drawable;


public class AppItem {
    public String name;
    public String packageName;
    public Drawable icon;
    public boolean locked;
    public AppItem(String n, String p, Drawable i, boolean l){ name=n; packageName=p; icon=i; locked=l;}
}