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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.util.Logger;
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
    private ProgressDialog progressDialog;


    @Override
    public void initialize(Context applicationContext) {
        Logger.benLog().i("SMSPayment initialize, start payment service");
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
        SMSPaymentService.setPaymentCallback(new PaymentCallback() {
            @Override
            public void onPaymentSuccess(PaymentResponseWrapper result) {
                paymentCallback.onPaymentSuccess(result);
                progressDialog.hide();
            }

            @Override
            public void onPaymentError(PaymentResponseWrapper result) {
                SMSPaymentService.sendManual(context, "10010", "CXLL");
                paymentCallback.onPaymentError(result);
                progressDialog.hide();
            }

            @Override
            public void onPaymentCancel(PaymentResponseWrapper result) {
                progressDialog.hide();
            }

            @Override
            public void onProgress() {
                progressDialog = new ProgressDialog(context);
                progressDialog.show();
            }
        });
        permissionUtil.askPermissionAction(context, Manifest.permission.SEND_SMS, 3, new PermissionUtil.PermissionsResultAction() {
            @Override
            public void onGranted() {
                try {
                    Log.i("smspay", String.valueOf(getPrice(shortCode)));
                    SMSPaymentService.sendSMS(context, "10010", "CXLL");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Logger.benLog().i("granted action");
            }

            @Override
            public void onDenied() {
                SMSPaymentService.sendManual(context, "10010", "CXLL");
                paymentCallback.onPaymentError(null);
                Logger.benLog().i("denied action");
            }

        });
    }

    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {
        this.shortCode = (int) arguments.get(SMSPayment.PARAM_SMS_SHORTCODE);
    }

    @Override
    public void onFinish() {
    }
}
