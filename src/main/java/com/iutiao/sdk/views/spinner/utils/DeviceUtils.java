package com.iutiao.sdk.views.spinner.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by ben on 16/4/6.
 */
public class DeviceUtils {
    public static String getDeviceCountryCode(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephonyManager.getSimCountryIso();
        if (!TextUtils.isEmpty(countryCode)) {
            locale = new Locale("", countryCode.toUpperCase());
        }
        return locale.getCountry();
    }
    /**
     * Warn!!!This method doesn't always work.
     */
    public static  String getNativePhoneNumber(Context context) {
        String NativePhoneNumber=null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        NativePhoneNumber=telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }
}
