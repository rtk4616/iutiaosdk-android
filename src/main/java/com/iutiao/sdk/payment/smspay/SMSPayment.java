/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment.smspay;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.util.PermissionUtil;

import java.util.Map;

/**
 * Created by Ben on 16/5/16.
 */
public class SMSPayment implements IPayment {

    public static final String PARAM_SMS_SHORTCODE = "param_sms_shortcode";
    private PermissionUtil permissionUtil;
    private Context context;
    private SMSOperator smsOperator;
    private PaymentCallback paymentCallback;
    private int shortCode;


    @Override
    public void initialize(Context applicationContext) {
        Intent startIntent = new Intent(applicationContext, SMSPaymentService.class);
        permissionUtil = PermissionUtil.getInstance(applicationContext);
        applicationContext.startService(startIntent);
        smsOperator = SMSOperator.init(applicationContext);
        context = applicationContext;
    }

    @Override
    public void setPaymentCallback(PaymentCallback listener) {
paymentCallback = listener;
    }

    public double getPrice(int shortCode) {
        return smsOperator.getPrice(shortCode);
    }

    @Override
    public void pay(final Activity context) {
        permissionUtil.askPermissionAction(context, Manifest.permission.SEND_SMS, 3, new PermissionUtil.PermissionsResultAction() {
            @Override
            public void onGranted() {
                try {
                    Log.i("smspay", String.valueOf(getPrice(shortCode)));
                    SMSPaymentService.sendSMS(context, "10010", "CXLcL", new PaymentCallback() {
                        @Override
                        public void onPaymentSuccess(PaymentResponseWrapper result) {
                            paymentCallback.onPaymentSuccess(result);
                        }

                        @Override
                        public void onPaymentError(PaymentResponseWrapper result) {
                            SMSPaymentService.sendManual(context, "10010", "CXLL");
                            paymentCallback.onPaymentError(result);
                        }

                        @Override
                        public void onPaymentCancel(PaymentResponseWrapper result) {
                        }

                        @Override
                        public void onProgress() {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("permission", "grantedAction");
            }

            @Override
            public void onDenied() {
                Log.i("permission", "deniedAction");
            }

        });
    }

    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {
        this.shortCode = (int) arguments.get(SMSPayment.PARAM_SMS_SHORTCODE);
    }

    @Override
    public void onFinish() {
        // TODO: 16/5/18  注意finish的时机
        Intent stopIntent = new Intent(context, SMSPaymentService.class);
        context.stopService(stopIntent);
    }
}
