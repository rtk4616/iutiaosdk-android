package com.iutiao.sdk;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.iutiao.sdk.exceptions.IUTiaoSdkNotInitializedException;

import java.util.Collection;

/**
 * Created by yxy on 15/11/4.
 */
public final class Validate {
    private static final String TAG = Validate.class.getName();

    private static final String NO_INTERNET_PERMISSION_REASON =
            "No internet permissions granted for the app, please add " +
            "<uses-permission android:name=\"android.permission.INTERNET\" /> " +
            "to your AndroidManifest.xml.";

    private static final String IUTIAO_ACTIVITY_NOT_FOUND_REASON =
            "IUTiaoActivity is not declared in the AndroidManifest.xml, please add " +
            "com.iutiao.sdk.IUTiaoActivity to your AndroidManifest.xml file.";

    public static void notNull(Object arg, String name) {
        if (arg == null) {
            throw new NullPointerException("Argument '" + name + "' cannot be null");
        }
    }

    public static <T> void notEmpty(Collection<T> container, String name) {
        if (container.isEmpty()) {
            throw new IllegalArgumentException("Container '" + name + "' cannot be empty");
        }
    }

    public static void hasInternetPermissions(Context context) {
        Validate.hasInternetPermissions(context, true);
    }

    public static void hasInternetPermissions(Context context, boolean shouldThrow) {
        Validate.notNull(context, "context");
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_DENIED) {
            if (shouldThrow) {
                throw new IllegalStateException(NO_INTERNET_PERMISSION_REASON);
            } else {
                Log.w(TAG, NO_INTERNET_PERMISSION_REASON);
            }
        }
    }

    public static void hasIUTiaoActivity(Context context) {
        Validate.hasIUTiaoActivity(context, true);
    }

    public static void hasIUTiaoActivity(Context context, boolean shouldThrow) {
        Validate.notNull(context, "context");
        PackageManager pm = context.getPackageManager();
        ActivityInfo activityInfo = null;
        if (pm != null) {
            ComponentName componentName =
                    new ComponentName(context, IUTiaoActivity.class);
            try {
                activityInfo = pm.getActivityInfo(
                        componentName,
                        PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        if (activityInfo == null) {
            if (shouldThrow) {
                throw new IllegalStateException(IUTIAO_ACTIVITY_NOT_FOUND_REASON);
            } else {
                Log.w(TAG, IUTIAO_ACTIVITY_NOT_FOUND_REASON);
            }
        }
    }

    public static  <E extends Enum<E>> void isInEnum(String value, Class<E> enumClass) {
        for (E e: enumClass.getEnumConstants()) {
            if (e.name().equals(value)) {
                return;
            }
        }
        throw new IllegalArgumentException(value + " is invalid");
    }

    public static void sdkInitialize() {
        if (!IUTiaoSdk.isInitialized()) {
            throw new IUTiaoSdkNotInitializedException(
                    "The SDK has not been initialized, make sure to call " +
                    " IUTiaoSdk.sdkInitialize() first.");
        }
    }

    public static String hasAppID() {
        String id = IUTiaoSdk.getApplicationId();
        if (id == null) {
            throw new IllegalArgumentException("no App ID found, please set the APP ID.");
        }
        return id;
    }
}