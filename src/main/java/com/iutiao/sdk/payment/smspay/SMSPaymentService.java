/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment.smspay;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SMSPaymentService extends Service  {

    public static final String TAG = "MyService";

    private static String RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static String SENT = "SMS_SENT";
    private static String DELIVERED = "SMS_DELIVERED";

    private static PaymentCallback paymentCallback;


    public static void sendSMS(Context context, String phoneNumber, String message,PaymentCallback paymentCallback) {
        SMSPaymentService.paymentCallback= paymentCallback;
        paymentCallback.onProgress();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        //如果短信内容过长时，则对短信内容进行拆分
        ArrayList<String> texts = sms.divideMessage(message);
        for (String text : texts) {
            try {
                sms.sendTextMessage(phoneNumber, null, text, sentPI, deliveredPI);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
            Log.i(TAG, "send" + text);
        }
    }

    public static void sendManual(Context context, String number, String message) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("sms_body", message);
        context.startActivity(sendIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcasts();
        Log.i(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() executed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public SMSPaymentService() {


    }

    private void registerBroadcasts() {
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Map<String,Object> response = new HashMap<String, Object>();
                PaymentResponseWrapper paymentResponseWrapper;
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        paymentResponseWrapper = new PaymentResponseWrapper(response,null);
                        paymentCallback.onPaymentSuccess(paymentResponseWrapper);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        paymentResponseWrapper = new PaymentResponseWrapper(response, null);
                        paymentCallback.onPaymentError(paymentResponseWrapper);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        paymentResponseWrapper = new PaymentResponseWrapper(response, null);
                        paymentCallback.onPaymentError(paymentResponseWrapper);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        paymentResponseWrapper = new PaymentResponseWrapper(response, null);
                        paymentCallback.onPaymentError(paymentResponseWrapper);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        paymentResponseWrapper = new PaymentResponseWrapper(response, null);
                        paymentCallback.onPaymentError(paymentResponseWrapper);
                        break;
                }
            }
        }, new IntentFilter(SENT));
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Log.i("test", "SmsReceiver->onReceive");
                Bundle bundle = arg1.getExtras();//获取intent中的内容
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    for (SmsMessage message : messages) {
                        String strFrom = message.getDisplayOriginatingAddress();
                        String strMsg = message.getDisplayMessageBody();
                        Log.i("test", "From:" + strFrom);
                        Log.i("test", "Msg:" + strMsg);
                    }
                }
            }
        }, new IntentFilter(RECEIVED));
    }
}
