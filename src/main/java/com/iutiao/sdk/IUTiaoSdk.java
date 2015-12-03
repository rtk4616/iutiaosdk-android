/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iutiao.IUTiao;
import com.iutiao.model.AppOrder;
import com.iutiao.net.RequestOptions;
import com.iutiao.sdk.exceptions.IUTiaoSdkException;
import com.iutiao.sdk.model.OrderInfo;
import com.iutiao.sdk.tasks.AppOrderTask;
import com.iutiao.sdk.util.TextUtils;
import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayInitCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by yxy on 15/11/3.
 */
public final class IUTiaoSdk {
    private static final String TAG = IUTiaoSdk.class.getCanonicalName();

    private static volatile Executor executor;

    private static final Object LOCK = new Object();

    private static volatile String applicationId;
    private static volatile String applicationName;
    private static volatile String appClientToken;
    private static Context applicationContext;
    private static volatile String currency;
    private static volatile boolean isDebugEnabled = BuildConfig.DEBUG;
    private static final int MAX_REQUEST_CODE_RANGE = 100;

    private static volatile String upayAppkey;

    /**
     * The key for the application ID in the Android manifest.
     */
    public static final String APPLICATION_ID_PROPERTY = "com.iutiao.sdk.ApplicationId";
    public static final String APPLICATION_NAME_PROPERTY = "com.iutiao.sdk.ApplicationName";
    public static final String CURRENCY_PROPERTY = "com.iutiao.sdk.currency";

    public static final String UPAY_APPKEY_PROPERTY = "UPAY_APPKEY";

    public enum CURRENCIES {
        rub
    }

    /**
     * The key for the client token in the Android manifest
     */
    public static final String CLIENT_TOKEN_PROPERTY = "com.iutiao.sdk.ClientToken";

    private static Boolean sdkInitialized = false;
    private static int callbackRequestCodeOffset = 0xafc;

    static final String CALLBACK_OFFSET_CHANGED_AFTER_INIT =
            "The callback request code offset can't be updated once the SDK is initialized";
    static final String CALLBACK_OFFSET_NEGATIVE =
            "The callback request code offset can't be negative.";
    /**
     * 初始化 sdk
     * @param context The application context
     * @param callbackRequestCodeOffset the request code offset that iutiao activities will be called with.
     */
    public static synchronized void sdkInitialize(Context context, int callbackRequestCodeOffset) {
        IUTiaoSdk.sdkInitialize(context, callbackRequestCodeOffset, null);
    }

    public static synchronized void sdkInitialize(
            Context context, int callbackRequestCodeOffset, InitializeCallback callback) {
        if (sdkInitialized && callbackRequestCodeOffset != IUTiaoSdk.callbackRequestCodeOffset) {
            throw new IUTiaoSdkException(CALLBACK_OFFSET_CHANGED_AFTER_INIT);
        }
        if (callbackRequestCodeOffset < 0) {
            throw new IUTiaoSdkException(CALLBACK_OFFSET_NEGATIVE);
        }
        IUTiaoSdk.callbackRequestCodeOffset = callbackRequestCodeOffset;
        sdkInitialize(applicationContext);
    }

    public static synchronized void sdkInitialize(Context applicationContext) {
        IUTiaoSdk.sdkInitialize(applicationContext, null);
    }

    /**
     * 初始化 sdk
     * @param applicationContext
     * @param callback 初始化完成后执行的回调，即使 sdk 已经被初始化过了， 这个回调依然会被执行
     */
    public static synchronized void sdkInitialize(
            Context applicationContext, final InitializeCallback callback) {
        if (sdkInitialized) {
            if (callback != null) {
                callback.onInitialized();
            }
        }
        Validate.notNull(applicationContext, "applicationContext");
        Validate.hasInternetPermissions(applicationContext, false);

        IUTiaoSdk.applicationContext = applicationContext.getApplicationContext();
        // 载入默认的设置
        IUTiaoSdk.loadDefaultsFromMetadata(IUTiaoSdk.applicationContext);

        sdkInitialized = true;
        iutiaoClientInitialize();
        upayInitialize();
//        initFloatView();
    }

    private static void upayInitialize() {
        // 初始化 upay 支付
        Upay up = Upay.initInstance(getApplicationContext(), null, null, null, null, new UpayInitCallback() {
            @Override
            public void onInitResult(int i, String s) {
                if (i == 200) {
                    Log.i(TAG, "upay initialized successful, result " + i + " response " + s);
                } else {
                    throw new IUTiaoSdkException(String.format("upay initialized failed, status_code: %d reason: %s", i, s));
                }
            }
        });
    }

    /*
     * 初始化 request client
     */
    private static void iutiaoClientInitialize() {
        // initialize iutiao client
        // 设置 api_key, access_token

        String token = AccessTokenManager.getInstance().getCurrentAccessToken();
        (new RequestOptions.RequestOptionsBuilder())
                .setAppKey(getApplicationId())
                .setToken(token)
                .build();
    }

    public static void setDebugMode() {
        IUTiao.setDebugMode();
        IUTiao.overrideApiBase("http://192.168.1.200:8000");
    }

    /**
     * Indicates whether the SDK has been initialized.
     * @return
     */
    public static synchronized boolean isInitialized() {
        return sdkInitialized;
    }

    public static boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public static void setIsDebugEnabled(boolean enabled) {
        isDebugEnabled = enabled;
    }

    public static Context getApplicationContext() {
        Validate.sdkInitialize();
        return applicationContext;
    }

    private static void loadDefaultsFromMetadata(Context context) {
        if (context == null) { return; }
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager()
                    .getApplicationInfo(
                            context.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        if (ai == null || ai.metaData == null) { return; }

        if (applicationId == null) {
            applicationId = ai.metaData.getString(APPLICATION_ID_PROPERTY);
        }
        if (applicationName == null) {
            applicationName = ai.metaData.getString(APPLICATION_NAME_PROPERTY);
        }
        if (currency == null) {
            String _currency = ai.metaData.getString(CURRENCY_PROPERTY);
            Validate.isInEnum(_currency, CURRENCIES.class);
            currency = _currency;
        }

        if (upayAppkey == null) {
            upayAppkey = String.valueOf(ai.metaData.getInt(UPAY_APPKEY_PROPERTY));
        }

    }

    public static String getUpayAppkey() {
        return upayAppkey;
    }

    public static String getCurrency() {
        return currency;
    }

    public static String getApplicationId() {
        return applicationId;
    }

    public static void setApplicationId(String applicationId) {
        IUTiaoSdk.applicationId = applicationId;
    }

    public static String getApplicationName() {
        Validate.sdkInitialize();
        return applicationName;
    }

    public static int getCallbackRequestCodeOffset() {
        Validate.sdkInitialize();
        return callbackRequestCodeOffset;
    }

    public static String getSdkVersion() {
        return IUTiaoSdkVersion.BUILD;
    }

    public static boolean isIUTiaoRequestCode(int requestCode) {
        return requestCode >= callbackRequestCodeOffset
                && requestCode < callbackRequestCodeOffset + MAX_REQUEST_CODE_RANGE;
    }

    /**
     * Callback passed to the sdkInitialize function.
     */

    public interface InitializeCallback {
        // sdk 初始化完成后调用
        void onInitialized();
    }

    public static void onDestory() {
        Upay upay = Upay.getInstance(getUpayAppkey());
        upay.exit();
    }

    private static WindowManager windowManager;
    private static WindowManager.LayoutParams windownLayoutParams;

    public static void initFloatView() {
        windowManager = (WindowManager) getApplicationContext().getSystemService("window");
        // 设置 LayoutParams（全局变量）相关参数
        windownLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE
        );
        windownLayoutParams.format = PixelFormat.RGBA_8888;
        windownLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windownLayoutParams.x = 400;
        windownLayoutParams.y = 400;
        windownLayoutParams.width = 80;
        windownLayoutParams.height = 80;
        createFloatView();

    }

    public static void createFloatView() {
        ImageView img = new ImageView(getApplicationContext());
        img.setImageResource(R.drawable.ic_launcher);
        img.setAlpha(80);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "floatable clickable");
            }
        });
        windownLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        windowManager.addView(img, windownLayoutParams);
    }

}
