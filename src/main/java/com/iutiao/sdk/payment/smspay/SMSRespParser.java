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
 * SMS response:ZTE.skuId.resultCode
 */
public class SMSRespParser {
    public static PaymentResponseWrapper parse(String strXmlResp) {
        PaymentResponseWrapper paymentResponseWrapper;
        Map<String, Object> data = new HashMap<>();
        String[] strXmlResps = strXmlResp.split("\\|", 3);
        if (strXmlResps.length != 3) {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Format error:" + strXmlResp));
            return paymentResponseWrapper;
        }

        String skuId = strXmlResps[1];
        String resultCode = strXmlResps[2];
        if(TextUtils.isEmpty(skuId)||TextUtils.isEmpty(resultCode)){
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Format error:" + strXmlResp));
            return paymentResponseWrapper;
        }

        if(resultCode.equals("0")){
            data.put(SMSPayment.RESP_SMS_SKU_ID, skuId);
            paymentResponseWrapper = new PaymentResponseWrapper(data, null);
            return paymentResponseWrapper;
        }else {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Payment error:"+resultCode));
            return paymentResponseWrapper;
        }
    }
}
