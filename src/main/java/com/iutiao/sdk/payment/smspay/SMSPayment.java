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
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iutiao.sdk.exceptions.IUTiaoSdkException;
import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.util.Logger;
import com.iutiao.sdk.util.PermissionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ben on 16/5/16.
 */
public class SMSPayment implements IPayment {

    public static final String PARAM_SMS_SHORTCODE = "param_sms_shortcode";
    public static final int MSG_PERMISSION_OVERTIME = 0x01;
    public static final int MSG_DELIVERED_OVERTIME = 0x02;
    private Timer timer;
    private PermissionUtil permissionUtil;
    private Context context;
    private SMSOperator smsOperator;
    private PaymentCallback paymentCallback;
    private int shortCode;
    private ProgressDialog progressDialog;
    private boolean isActionExcuted = false;
    private boolean isActionResulted = false;
    private Handler timerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PERMISSION_OVERTIME:
                    if (isActionResulted||isActionExcuted) {
                        break;
                    }
                case MSG_DELIVERED_OVERTIME:
                    if(isActionResulted){
                        break;
                    }
                    if (progressDialog != null) {
                        progressDialog.hide();
                    }
                    if (paymentCallback != null) {
                        paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("overtime")));
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void initialize(Context applicationContext) {
        Logger.benLog().i("SMSPayment initialize, start payment service");
        Intent startIntent = new Intent(applicationContext, SMSPaymentService.class);
        permissionUtil = PermissionUtil.getInstance(applicationContext);
        applicationContext.startService(startIntent);
        smsOperator = SMSOperator.init(applicationContext);
        timer = new Timer(true);
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
                isActionResulted = true;
                paymentCallback.onPaymentSuccess(result);
                progressDialog.hide();
            }

            @Override
            public void onPaymentError(PaymentResponseWrapper result) {
                SMSPaymentService.sendManual(context, "10010", "CXLL");
                isActionResulted = true;
                paymentCallback.onPaymentError(result);
                if (progressDialog != null) {
                    progressDialog.hide();
                }
            }

            @Override
            public void onPaymentCancel(PaymentResponseWrapper result) {
                isActionResulted = true;
                progressDialog.hide();
            }

            @Override
            public void onProgress() {
                isActionExcuted = true;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timerHandler.obtainMessage(MSG_DELIVERED_OVERTIME).sendToTarget();
                    }
                }, 6000L);
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
                paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("action to send sms was denied")));
                Logger.benLog().i("denied action");
            }

        });
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerHandler.obtainMessage(MSG_PERMISSION_OVERTIME).sendToTarget();
            }
        }, 6000L);
    }

    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {
        this.shortCode = (int) arguments.get(SMSPayment.PARAM_SMS_SHORTCODE);
    }

    @Override
    public void onFinish() {
    }
}
