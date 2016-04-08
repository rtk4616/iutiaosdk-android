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
import android.telephony.TelephonyManager;

import java.util.Random;

/**
 * Created by yxy on 15/11/4.
 */
public final class Utility {

    static String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateRandomString(int length) {
        Random rand = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<length; i++) {
            int idx = Math.abs((rand.nextInt() % alphanumeric.length()));
            buffer.append(alphanumeric.charAt(idx));
        }
        return buffer.toString();
    }


    public static String getMyPhoneNumber() {
        TelephonyManager mManager =(TelephonyManager) IUTiaoSdk.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        return mManager.getLine1Number();
    }

//    public static void checkPhoneField(EditText view) {
//        if (Validate.isPhoneValid(view.getText().toString().trim())) {
//            view.setError("invalid phone number");
//        }
//    }
}
