package com.iutiao.sdk;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.iutiao.sdk.exceptions.IUTiaoSdkException;

/**
 * Created by yxy on 15/11/3.
 */
public final class IUTiaoSdk {
    private static final String TAG = IUTiaoSdk.class.getCanonicalName();

    private static final Object LOCK = new Object();

    private static volatile String applicationId;
    private static volatile String applicationName;
    private static volatile String appClientToken;
    private static Context applicationContext;
    private static volatile String currency;
    private static volatile boolean isDebugEnabled = BuildConfig.DEBUG;
    private static final int MAX_REQUEST_CODE_RANGE = 100;

    /**
     * The key for the application ID in the Android manifest.
     */
    public static final String APPLICATION_ID_PROPERTY = "com.iutiao.sdk.ApplicationId";
    public static final String APPLICATION_NAME_PROPERTY = "com.iutiao.sdk.ApplicationName";
    public static final String CURRENCY_PROPERTY = "com.iutiao.sdk.currency";

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
}
