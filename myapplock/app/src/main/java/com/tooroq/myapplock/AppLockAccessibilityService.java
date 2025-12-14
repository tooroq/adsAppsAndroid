package com.tooroq.myapplock;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.app.ActivityManager;
import android.content.Context;
import java.util.Set;

public class AppLockAccessibilityService extends AccessibilityService {
    public static String currentlyUnlockedPackage = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null) return;
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            CharSequence pkg = event.getPackageName();
            if(pkg == null) return;
            String packageName = pkg.toString();

            // تجاهل تطبيقنا
            if(packageName.equals(getPackageName())) return;

            // إذا ضغط المستخدم على Home (سيكون اسم الحزمة هو launcher) — نظف unlocked
            if(isLauncherPackage(packageName)){
                currentlyUnlockedPackage = null;
                return;
            }

            Set<String> locked = Prefs.getLockedApps(this);
            if(locked.contains(packageName)){
                // إذا لم يكن هذا التطبيق مُفْتَوحاً مؤقتاً، اطلب PIN
                if(!packageName.equals(currentlyUnlockedPackage)){
                    // إطلاق نشاط طلب PIN
                    Intent i = new Intent(this, PinUnlockActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("target", packageName);
                    startActivity(i);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {}

    private boolean isLauncherPackage(String pkg){
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ComponentName cn = intent.resolveActivity(pm);
        if(cn == null) return false;
        return cn.getPackageName().equals(pkg);
    }
}
