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
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.iutiao.sdk.exceptions.IUTiaoSdkException;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.util.Logger;
import com.iutiao.sdk.util.PermissionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SMSPaymentService extends Service {
    public static final String RESP_SMS_SKU_ID = "resp_sms_sku_id";
    private static String RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static String SENT = "SMS_SENT";
    private static String DELIVERED = "SMS_DELIVERED";

    private static String SMS_SHORTCODE_1 = "4446";
    private static String SMS_SHORTCODE_3 = "4449";

    private static final String SMS_MESSAGE_PREFIX = "HIBB";

    private static SMSPaymentCallback paymentCallback;
    private static ProgressDialog progressDialog;
    public static Map<String, Object> responseData;
    private static PermissionUtil permissionUtil;

    public static final int MSG_PERMISSION_OVERTIME = 0x01;
    public static final int MSG_DELIVERED_OVERTIME = 0x02;
    private static Handler timerHandler = new Handler() {
        public void handleMessage(Message msg) {
            dimissProgress();
            switch (msg.what) {
                case MSG_PERMISSION_OVERTIME:
                    if (paymentCallback != null) {
                        paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("permission-overtime")));
                    }
                    break;
                case MSG_DELIVERED_OVERTIME:
                    if (progressDialog != null) {
                        progressDialog.hide();
                    }
                    if (paymentCallback != null) {
                        paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("deliver-overtime")));
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };
    private static Timer timer = new Timer();
    private static TimerTask permissionTime;
    private static TimerTask deliverTime;

    public static void setPaymentCallback(SMSPaymentCallback paymentCallback) {
        SMSPaymentService.paymentCallback = paymentCallback;
    }

    public static void init(Context applicationContext) {
        Logger.benLog().i("SMSPayment initialize, start payment service");
        Intent startIntent = new Intent(applicationContext, SMSPaymentService.class);
        applicationContext.startService(startIntent);
        permissionUtil = PermissionUtil.getInstance(applicationContext);
    }

    public static void addAmount1(final Activity activity) {
        showProgress(activity);
        permissionUtil.askPermissionAction(activity, Manifest.permission.SEND_SMS, 3, new PermissionUtil.PermissionsResultAction() {
            @Override
            public void onGranted() {
                Logger.benLog().i("granted action");
                try {
                    responseData = new Hashtable<>();
                    sendSMS(activity, SMS_SHORTCODE_1, SMS_MESSAGE_PREFIX + activity.getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDenied() {
                dimissProgress();
                sendManual(activity, SMS_SHORTCODE_1, SMS_MESSAGE_PREFIX + activity.getPackageName());
                paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("action to send sms was denied")));
                Logger.benLog().i("denied action");
            }

        });
        if (permissionTime != null) {
            permissionTime.cancel();
        }
        permissionTime = new PermissionTime();
        timer.schedule(permissionTime, 6000L);

    }

    private static void showProgress(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private static void dimissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    public static void addAmount3(final Activity activity) {
        showProgress(activity);
        permissionUtil.askPermissionAction(activity, Manifest.permission.SEND_SMS, 3, new PermissionUtil.PermissionsResultAction() {
            @Override
            public void onGranted() {
                Logger.benLog().i("granted action");
                try {
                    responseData = new Hashtable<>();
                    sendSMS(activity, SMS_SHORTCODE_3, SMS_MESSAGE_PREFIX + activity.getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDenied() {
                dimissProgress();
                sendManual(activity, SMS_SHORTCODE_3, SMS_MESSAGE_PREFIX + activity.getPackageName());
                paymentCallback.onPaymentError(new PaymentResponseWrapper(new HashMap<String, Object>(), new IUTiaoSdkException("action to send sms was denied")));
                Logger.benLog().i("denied action");
            }

        });
        if (permissionTime != null) {
            permissionTime.cancel();
        }
        permissionTime = new PermissionTime();
        timer.schedule(permissionTime, 6000L);
    }


    public static void sendSMS(Context context, String phoneNumber, String message) {
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
            Logger.benLog().i("start to send, message:" + text);
        }
    }

    public static void sendManual(final Context context, final String number, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("To pay by sms, check if you have allowed the permission to send sms.");
        builder.setPositiveButton("Check", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
//                sendIntent.setData(Uri.parse("smsto:" + number));
//                sendIntent.putExtra("sms_body", message);
//                context.startActivity(sendIntent);
                PermissionUtil.getInstance(context).startAppSettings(context);
            }
        });
        builder.setNegativeButton("Send manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:" + number));
                sendIntent.putExtra("sms_body", message);
                context.startActivity(sendIntent);
            }
        });
        builder.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcasts();
        Logger.benLog().i("SMSPaymentService onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.benLog().i("SMSPaymentService onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.benLog().i("SMSPaymentService onDestroy()");
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
                PaymentResponseWrapper paymentResponseWrapper;
                permissionTime.cancel();
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        if (progressDialog!=null){
                            progressDialog.show();
                        }
                        if (deliverTime != null) {
                            deliverTime.cancel();
                        }
                        deliverTime = new DeliveredTime();
                        timer.schedule(deliverTime, 6000L);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        dimissProgress();
                        if (paymentCallback != null) {
                            paymentResponseWrapper = new PaymentResponseWrapper(responseData, new IUTiaoSdkException("Generic failure"));
                            paymentCallback.onPaymentError(paymentResponseWrapper);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        dimissProgress();
                        if (paymentCallback != null) {
                            paymentResponseWrapper = new PaymentResponseWrapper(responseData, new IUTiaoSdkException("No service"));
                            paymentCallback.onPaymentError(paymentResponseWrapper);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        dimissProgress();
                        if (paymentCallback != null) {
                            paymentResponseWrapper = new PaymentResponseWrapper(responseData, new IUTiaoSdkException("Null PDU"));
                            paymentCallback.onPaymentError(paymentResponseWrapper);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        dimissProgress();
                        if (paymentCallback != null) {
                            paymentResponseWrapper = new PaymentResponseWrapper(responseData, new IUTiaoSdkException("Radio off"));
                            paymentCallback.onPaymentError(paymentResponseWrapper);
                        }
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
                        Logger.benLog().i("SmsReceiver->onDelivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        Logger.benLog().i("SmsReceiver->fail delivered");
                        dimissProgress();
                        if (paymentCallback != null) {
                            PaymentResponseWrapper paymentResponseWrapper;
                            paymentResponseWrapper = new PaymentResponseWrapper(responseData, new IUTiaoSdkException("Radio off"));
                            paymentCallback.onPaymentError(paymentResponseWrapper);
                        }
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Logger.benLog().i("SmsReceiver->onReceive");
                deliverTime.cancel();
                String strXmlResp = "";
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
                        strXmlResp = strXmlResp = strMsg;
                    }
                }

                if (!SMSRespParser.isPayResp(strXmlResp, getPackageName())) return;
                if (paymentCallback == null) return;

                PaymentResponseWrapper paymentResponseWrapper = SMSRespParser.parse(strXmlResp);
                if (paymentResponseWrapper.error != null) {
                    paymentCallback.onPaymentError(paymentResponseWrapper);
                } else {
                    if (paymentResponseWrapper.data.get(SMSPaymentService.RESP_SMS_SKU_ID).equals(SMS_SHORTCODE_1)){
                        paymentCallback.onAddAmount1(paymentResponseWrapper);
                    }
                    if (paymentResponseWrapper.data.get(SMSPaymentService.RESP_SMS_SKU_ID).equals(SMS_SHORTCODE_3)){
                        paymentCallback.onAddAmount3(paymentResponseWrapper);
                    }
                }
                dimissProgress();
            }
        }, new IntentFilter(RECEIVED));
    }

    static class PermissionTime extends TimerTask {
        @Override
        public void run() {
            timerHandler.obtainMessage(MSG_PERMISSION_OVERTIME).sendToTarget();
        }
    }

    class DeliveredTime extends TimerTask {
        @Override
        public void run() {
            timerHandler.obtainMessage(MSG_DELIVERED_OVERTIME).sendToTarget();
        }
    }
}
