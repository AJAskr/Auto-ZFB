package com.mx.accessibilitydemo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import java.util.List;

public class AccessibilityUtils {
    /**
     * 跳转到无障碍设置界面
     *
     * @param context 上下文
     */
    public static void gotoSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断是否开启无障碍服务
     */
    public static boolean isOpenAccessibility(Context context, String className) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices =
                activityManager.getRunningServices(100);
        if (runningServices.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            if (info.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
