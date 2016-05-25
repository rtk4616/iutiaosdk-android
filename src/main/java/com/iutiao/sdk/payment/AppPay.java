/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment;

import android.app.Activity;
import android.content.Context;

import com.iutiao.sdk.payment.smspay.SMSPayment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ben on 16/5/18.
 */
public class AppPay {
    public static final int PAY_METHOD_SMS = 0;
    public static final int PAY_METHOD_UPAY = 1;
    public static final int PAY_METHOD_PASSION = 2;
    private static int cuurentPayMethod = -1;

    // TODO: 16/5/18 passion和upay的param字段定义
    // TODO: 16/5/18 paymethod 定义

    public static SMSPayment smsPayment;

    /**
     * 单机支付Jar中，在应用启动页先调用此初始化接口；
     * 或使用IUTiaoSDK，会自动完成调用。
     */
    public static void init(Context context) {
// TODO: 16/5/18 sdk 的初始化代码段和支付方式列表
        smsPayment = new SMSPayment();
        smsPayment.initialize(context);
    }


    public static void addAmount1(Activity activity, String sku_id, PaymentCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(SMSPayment.PARAM_SMS_SKU_ID,sku_id);
        params.put(SMSPayment.PARAM_SMS_SHORTCODE,SMSPayment.SHORTCODE_1);
        startPay(activity, params, callback, PAY_METHOD_SMS);
    }

    public static void addAmount3(Activity activity, String sku_id, PaymentCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(SMSPayment.PARAM_SMS_SKU_ID,sku_id);
        params.put(SMSPayment.PARAM_SMS_SHORTCODE,SMSPayment.SHORTCODE_3);
        startPay(activity, params, callback, PAY_METHOD_SMS);
    }


    public static void startPay(Activity activity, String sku_id, PaymentCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put(SMSPayment.PARAM_SMS_SKU_ID,sku_id);
        startPay(activity, params, callback, PAY_METHOD_SMS);//单机支付Jar中默认sms支付
    }
    /**
     * 收银台版本支付接口，将调起收银台
     *
     * @param activity
     * @param params
     * @param callback
     */
    public static void startPay(Activity activity, Map<String, Object> params, PaymentCallback callback) {
        startPay(activity, params, callback, PAY_METHOD_SMS);//单机支付Jar中默认sms支付
    }

    /**
     * 无收银台版本支付接口
     *
     * @param activity
     * @param params
     * @param callback
     * @param payMethod
     */
    private static void startPay(Activity activity, Map<String, Object> params, PaymentCallback callback, int payMethod) {
        switch (payMethod){
            case PAY_METHOD_SMS:
                smsPayment.setPaymentCallback(callback);
                smsPayment.setPaymentArguments(params);
                smsPayment.pay(activity);
                break;
            case PAY_METHOD_PASSION:
                // TODO: 16/5/18
                break;
            case PAY_METHOD_UPAY:
                break;
            default:
                break;
        }
    }


}
