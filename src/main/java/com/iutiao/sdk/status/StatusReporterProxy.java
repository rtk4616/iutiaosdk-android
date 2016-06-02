/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.status;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ly.count.android.sdk.Countly;

/**
 * Created by Ben on 16/6/1.
 */
public class StatusReporterProxy {
    private static final String serverUrl = "http://sentry.iutiao.com";
    private static final String appKey = "8a12ae84fa1a03cfef086c4fb2a509f96f5704f1";
    private static String pkgName = null;

    private static final String CRASH_PARAM_PKG_NAME = "PKG_NAME";
    private static final String CRASH_PARAM_VERSION_NAME = "VERSION_NAME";
    private static final String CRASH_PARAM_VERSION_CODE = "VERSION_CODE";
    private static final String CRASH_PARAM_BOARD = "BOARD";//主板
    private static final String CRASH_PARAM_BOOTLOADER = "BOOTLOADER";//启动加载器
    private static final String CRASH_PARAM_BRND = "BRND";//android系统定制商
    private static final String CRASH_PARAM_DEVICE = "DEVICE";// 设备参数
    private static final String CRASH_PARAM_HARDWARE = "HARDWARE"; // 显示屏参数
    private static final String CRASH_PARAM_DISPLAY = "DISPLAY"; // 显示屏参数
    private static final String CRASH_PARAM_FINGERPRIINT = "FINGERPRIINT";// 硬件名称
    private static final String CRASH_PARAM_RADIOVERSION = "RADIOVERSION";
    private static final String CRASH_PARAM_RADIO = "RADIO";
    private static final String CRASH_PARAM_MANUFACTURER = "MANUFACTURER";// 硬件制造商
    private static final String CRASH_PARAM_ID = "ID";// 修订版本列表
    private static final String CRASH_PARAM_MODEL = "MODEL";// 版本
    private static final String CRASH_PARAM_PRODUCT = "PRODUCT";// 手机制造商
    private static final String CRASH_PARAM_SERIAL = "SERIAL";
    private static final String CRASH_PARAM_CPU_ABI = "CPU_ABI";// cpu指令集
    private static final String CRASH_PARAM_CPU_ABI2 = "CPU_ABI2";

    private static final String KEY_PKGNAME = "PKG_NAME";

    private static final String USER_DATA_NETWORK_TYPR = "USER_DATA_NETWORK_TYPR";
    private static final String USER_DATA_COUNTRY = "USER_DATA_COUNTRY";

    private static int count = 0;

    private StatusReporterProxy() {
    }

    private static class SingletonHolder {
        static final StatusReporterProxy instance = new StatusReporterProxy();
    }

    public static StatusReporterProxy init(Context context) {
        Countly.sharedInstance()
                .init(context, serverUrl, appKey);
        pkgName = context.getPackageName()+"|";
        StatusReporterProxy.sharedInstance().setUserData(context);// If UserData plugin is enabled on your server
        StatusReporterProxy.sharedInstance().enableCrashTracking(context);
        ((Application) context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    StatusReporterProxy.sharedInstance().recordEvent("start",1);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });
        return SingletonHolder.instance;
    }

    public static StatusReporterProxy sharedInstance() {
        return SingletonHolder.instance;
    }

    public void setLoggingEnabled(boolean enableLogging) {
        Countly.sharedInstance().setLoggingEnabled(true);
    }
    public void recordEvent(String key) {
        this.recordEvent(key, (Map) null, 1, 0.0D);
    }
    public void recordEvent(String key, int count) {
        Countly.sharedInstance().recordEvent(pkgName+key, count);
    }

    public void recordEvent(String key, int count, double sum) {
        Countly.sharedInstance().recordEvent(pkgName+key, count, sum);
    }

    public void recordEvent(String key, Map<String, String> segmentation, int count) {
        Countly.sharedInstance().recordEvent(pkgName+key, segmentation, count);
    }

    public void recordEvent(String key, Map<String, String> segmentation, int count, double sum) {
        Countly.sharedInstance().recordEvent(pkgName+key, segmentation, count, sum);
    }
    public void setUserData(Context context){
        HashMap<String, String> data = new HashMap<String, String>();
        //providing any custom key values to store with user
        HashMap<String, String> custom = new HashMap<String, String>();
        custom.put(USER_DATA_COUNTRY, Locale.getDefault().getCountry());
        custom.put(KEY_PKGNAME, context.getPackageName());
        custom.put(USER_DATA_NETWORK_TYPR, NetWorkUtil.getCurrentNetworkType(context));
        //set multiple custom properties
        Countly.userData.setUserData(data, custom);
        Countly.userData.save();
    }

    public void enableCrashTracking(Context context) {
        HashMap<String, String> data = new HashMap<String, String>();
        collectDeviceInfo(context,data);
        Countly.sharedInstance().setCustomCrashSegments(data);
        Countly.sharedInstance().enableCrashReporting();
    }

    private void collectDeviceInfo(Context context,HashMap<String, String> infos) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = pi.versionName == null ? "null" : pi.versionName;
            String versionCode = pi.versionCode + "";
            infos.put(CRASH_PARAM_VERSION_NAME, versionName);
            infos.put(CRASH_PARAM_VERSION_CODE, versionCode);
            infos.put(CRASH_PARAM_PKG_NAME, context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        infos.put(CRASH_PARAM_BOARD, Build.BOARD);
        infos.put(CRASH_PARAM_BOOTLOADER, Build.BOOTLOADER);
        infos.put(CRASH_PARAM_BRND, Build.BRAND);
        infos.put(CRASH_PARAM_DEVICE, Build.DEVICE);
        infos.put(CRASH_PARAM_DISPLAY, Build.DISPLAY);
        infos.put(CRASH_PARAM_FINGERPRIINT, Build.FINGERPRINT);
        infos.put(CRASH_PARAM_RADIOVERSION, Build.getRadioVersion());
        infos.put(CRASH_PARAM_HARDWARE, Build.HARDWARE);
        infos.put(CRASH_PARAM_ID,Build.ID);
        infos.put(CRASH_PARAM_MANUFACTURER, Build.MANUFACTURER);
        infos.put(CRASH_PARAM_MODEL, Build.MODEL);
        infos.put(CRASH_PARAM_PRODUCT, Build.PRODUCT);
        infos.put(CRASH_PARAM_SERIAL, Build.SERIAL);
        infos.put(CRASH_PARAM_CPU_ABI, Build.CPU_ABI);
        infos.put(CRASH_PARAM_CPU_ABI2, Build.CPU_ABI2);
        infos.put(CRASH_PARAM_RADIO, Build.RADIO);
    }


    public static void onCreate(Activity activity) {
        Countly.onCreate(activity);
    }

    public void onStart(Activity activity) {
        Countly.sharedInstance().onStart(activity);
    }

    public void onStop() {
        Countly.sharedInstance().onStop();
    }
}
