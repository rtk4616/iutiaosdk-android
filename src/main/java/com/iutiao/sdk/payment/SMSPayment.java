/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iutiao.sdk.util.PermissionUtil;

import java.util.Map;

/**
 * Created by Ben on 16/5/16.
 */
public class SMSPayment implements IPayment {

    private PermissionUtil permissionUtil ;
    private Context context;

    @Override
    public void initialize(Context applicationContext) {
        Intent startIntent = new Intent(applicationContext, SMSPaymentService.class);
        permissionUtil = new PermissionUtil(applicationContext);
        applicationContext.startService(startIntent);
        context = applicationContext;
    }

    @Override
    public void setPaymentCallback(PaymentCallback listener) {

    }

    @Override
    public void pay(final Activity context) {
        permissionUtil.askPermissionAction(context, Manifest.permission.SEND_SMS, 3, new PermissionUtil.PermissionsResultAction() {
            @Override
            public void onGranted() {
                try {
                    SMSPaymentService.sendSMS(context, "10010", "CXLL");
                } catch (Exception e) {
                    Log.i("permission", e.getMessage());
                }

                Log.i("permission", "grantedAction");
//                        SMSPaymentService.sendManual(MainActivity.this, "10010", "CXLL");
            }

            @Override
            public void onDenied() {
                Log.i("permission", "deniedAction");
            }

        });
    }

    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {

    }

    @Override
    public void onFinish() {
        Intent stopIntent = new Intent(context, SMSPaymentService.class);
        context.stopService(stopIntent);
    }
}
