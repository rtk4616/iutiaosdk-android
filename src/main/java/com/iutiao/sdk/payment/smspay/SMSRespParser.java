/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment.smspay;

import com.iutiao.sdk.payment.PaymentResponseWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Successful SMS response:
 * <Response>
 * <SmsText>answer message</SmsText>
 * </Response>
 * <p/>
 * Error SMS response:
 * <Response>
 * <ErrorText>error message</ErrorText> <SmsText>answer message</SmsText>
 * </Response>
 * <p/>
 * This class can parse the xml response to PaymentResponseWrapper
 * Created by Ben on 16/5/23.
 */
public class SMSRespParser {
    public static PaymentResponseWrapper parse(String strXmlResp) {
        PaymentResponseWrapper paymentResponseWrapper;
        Map<String, Object> data = new HashMap<>();
        String errorText = null;
        String smsText = null;
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(strXmlResp));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("ErrorText")) {
                        errorText = xpp.nextText();
                    }
                    if (xpp.getName().equals("SmsText")) {
                        smsText = xpp.nextText();
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Server error，response is not xml format"));
            return paymentResponseWrapper;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (errorText == null && smsText == null) {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception("Server error，" + strXmlResp));
            return paymentResponseWrapper;
        }
        if (errorText == null) {
            data.put(SMSPayment.RESP_SMS_SKU_ID, smsText);
            paymentResponseWrapper = new PaymentResponseWrapper(data, null);
            return paymentResponseWrapper;
        } else {
            paymentResponseWrapper = new PaymentResponseWrapper(data, new Exception(errorText));
            return paymentResponseWrapper;
        }

    }
}
