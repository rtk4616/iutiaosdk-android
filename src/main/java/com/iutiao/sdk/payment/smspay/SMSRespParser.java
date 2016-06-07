/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment.smspay;

import android.text.TextUtils;

import com.iutiao.sdk.payment.PaymentResponseWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * SMS response example : ZTE|com.mega.newstarwars.forgame|4446|0
 */
public class SMSRespParser {
    public static final String SMS_RESP_SEPARATOR = "\\|";
    public static final String SMS_RESP_PREFIX = "ZTE|";
    public static final String SMS_RESP_RESULT_CODE_SUCCESS = "0";

    public static boolean isPayResp(String strXmlResp, String pkgName){
        return strXmlResp.startsWith(SMS_RESP_PREFIX+pkgName);
    }
    public static PaymentResponseWrapper parse(String strXmlResp) {
        PaymentResponseWrapper paymentResponseWrapper;
        Map<String, Object> data = new HashMap<>();
        String[] strXmlResps = strXmlResp.split(SMS_RESP_SEPARATOR, 4);
        if (strXmlResps.length != 4) {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Format error:" + strXmlResp));
            return paymentResponseWrapper;
        }

        String skuId = strXmlResps[2];
        String resultCode = strXmlResps[3];
        if(TextUtils.isEmpty(skuId)||TextUtils.isEmpty(resultCode)){
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Format error:" + strXmlResp));
            return paymentResponseWrapper;
        }

        if(resultCode.equals(SMS_RESP_RESULT_CODE_SUCCESS)){
            data.put(SMSPaymentService.RESP_SMS_SKU_ID, skuId);
            paymentResponseWrapper = new PaymentResponseWrapper(data, null);
            return paymentResponseWrapper;
        }else {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Payment error:"+resultCode));
            return paymentResponseWrapper;
        }
    }
}
