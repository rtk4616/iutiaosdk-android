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
import android.os.Bundle;

import com.google.gson.Gson;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.tasks.CrashLogTask;
import com.iutiao.sdk.util.IUTPreferencesUtils;
import com.iutiao.sdk.util.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 在Application中init，自动记录日常信息和Crash信息
 * Created by Ben on 16/5/27.
 */
public class StatusReporter {
    private static String SP_KEY_START_TIME = "sp_key_start_time";
    private static String SP_KEY_START_NETWORK = "sp_key_start_network";
    private static String SP_KEY_END_TIME = "sp_key_end_time";
    private static String SP_KEY_END_NETWORK = "sp_key_end_network";
    private static String SP_KEY_DUR_TIME = "sp_key_dur_time";
    private static String SP_KEY_STARTUP_COUNT = "sp_key_startup_count";
    private static String SP_KEY_COUNT_TIME = "sp_key_count_time";

    private static String PARAM_START_TIME = "PARAM_START_TIME";
    private static String PARAM_START_NETWORK = "PARAM_START_NETWORK";
    private static String PARAM_END_NETWORK = "PARAM_END_NETWORK";
    private static String PARAM_DUR_TIME = "PARAM_DUR_TIME";
    private static String PARAM_STARTUP_COUNT = "PARAM_STARTUP_COUNT";

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static int count = 0;

    private static ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();


    public static void sendToServer(Context ctx) {
        long startTime = IUTPreferencesUtils.getLong(ctx, SP_KEY_START_TIME);
        long durTime = IUTPreferencesUtils.getLong(ctx, SP_KEY_DUR_TIME);
        int startUpCount = IUTPreferencesUtils.getInt(ctx, SP_KEY_STARTUP_COUNT);

        String formatStartTime = formatter.format(new Date(startTime));
        String readableDurTime = durTime / (60 * 1000) + "分钟";

        String startNetworkType = IUTPreferencesUtils.getString(ctx, SP_KEY_START_NETWORK);
        String endNetworkType = IUTPreferencesUtils.getString(ctx, SP_KEY_END_NETWORK, "未检测");
        Logger.benLog().i("\n启动时间：" + formatStartTime + "\n"
                + "停留时间：" + readableDurTime + "\n"
                + "日启动数：" + startUpCount + "次\n"
                + "启动时网络：" + startNetworkType + "\n"
                + "退出时网络：" + endNetworkType + "\n");
    }

    private static void sendCrashToServer(Context mContext, String result) {
        CrashLogTask crashLogTask = new CrashLogTask(mContext, new IUTiaoCallback() {
            @Override
            public void onSuccess(Object t) {
                Logger.benLog().i(t);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onExecuted() {

            }
        });
        crashLogTask.execute(result);
    }

    public static void init(Context applicationContext) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        //注册crashHandler
        crashHandler.init(applicationContext, new CrashLogedListener() {
            @Override
            public void afterSaveCrash(Context mContext, Map<String, String> crashInfos) {
                Logger.benLog().i("崩溃");
                Gson gson = new Gson();
                String jsonStr = gson.toJson(crashInfos);
                sendCrashToServer(mContext, jsonStr);
            }
        });
        ((Application) applicationContext).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    StatusReporter.finish(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    StatusReporter.start(activity);

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
    }

    public static void start(final Context applicationContext) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                saveStartTime(applicationContext);
                saveDailyStartUpCount(applicationContext);
                saveStartNetwork(applicationContext);
                sendToServer(applicationContext);
            }
        });
    }

    private static void saveDailyStartUpCount(Context applicationContext) {
        long oldTime = IUTPreferencesUtils.getLong(applicationContext, SP_KEY_COUNT_TIME, new Date().getTime());
        try {
            if (TimeUtil.isYeaterday(new Date(oldTime), new Date()) == -1) {
                int count = IUTPreferencesUtils.getInt(applicationContext, SP_KEY_STARTUP_COUNT, 0);
                count++;
                IUTPreferencesUtils.putInt(applicationContext, SP_KEY_STARTUP_COUNT, count);
            } else {
                IUTPreferencesUtils.putInt(applicationContext, SP_KEY_STARTUP_COUNT, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        IUTPreferencesUtils.putLong(applicationContext, SP_KEY_COUNT_TIME, new Date().getTime());
    }

    private static void saveStartTime(Context applicationContext) {
        long startTime = new Date().getTime();
        IUTPreferencesUtils.putLong(applicationContext, SP_KEY_START_TIME, startTime);
    }

    public static void finish(final Context ctx) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                saveEndNDurTime(ctx);
                saveEndNetwork(ctx);
            }
        });
    }

    private static void saveEndNDurTime(Context ctx) {
        long endTime = new Date().getTime();
        long startTime = IUTPreferencesUtils.getLong(ctx, SP_KEY_START_TIME);
        long durTime = endTime - startTime;
        IUTPreferencesUtils.putLong(ctx, SP_KEY_END_TIME, endTime);
        IUTPreferencesUtils.putLong(ctx, SP_KEY_DUR_TIME, durTime);
    }

    private static void saveStartNetwork(Context ctx) {
        IUTPreferencesUtils.putString(ctx, SP_KEY_START_NETWORK, NetWorkUtil.getCurrentNetworkType(ctx));
    }

    private static void saveEndNetwork(Context ctx) {
        IUTPreferencesUtils.putString(ctx, SP_KEY_END_NETWORK, NetWorkUtil.getCurrentNetworkType(ctx));
    }

}
