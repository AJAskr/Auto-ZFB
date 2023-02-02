package com.mx.accessibilitydemo.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

public class PackageManagerUtils {
    /**
     * 获取当前Activity名称
     *
     * @param context            上下文
     * @param accessibilityEvent 无障碍事件
     * @return activity名称
     */
    public static String getActivityName(Context context, AccessibilityEvent accessibilityEvent) {
        ComponentName componentName = new ComponentName(
                accessibilityEvent.getPackageName().toString(),
                accessibilityEvent.getClassName().toString()
        );
        try {
            String activityName = context.getPackageManager().getActivityInfo(componentName, 0).toString();
            return activityName.substring(activityName.indexOf(" "), activityName.indexOf("}"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
