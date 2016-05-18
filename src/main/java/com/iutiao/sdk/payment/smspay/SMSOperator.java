/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment.smspay;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Ben on 16/5/17.
 */
public class SMSOperator {
    private static String IUT_SMS_OPERATORS = "iut_sms_operators.json";
    private static SMSOperator smsOperator;
    private String mccmnc;
    private String country;
    private String networkId;
    private String operatorName;
    private int shortCode;
    private HashMap code2price = new HashMap<Integer, Double>();

    private SMSOperator() {
    }

    public double getPrice(int shortCode) {
        return (double) code2price.get(shortCode);
    }

    public static SMSOperator init(Context context) {
        if (smsOperator == null) {
            smsOperator = new SMSOperator();
            readLocalMCCMNC(context,smsOperator);
            buildFromJson(context,smsOperator);
        }
        return smsOperator;
    }

    private static void readLocalMCCMNC(Context context,SMSOperator smsOperator) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        smsOperator.mccmnc = telManager.getSimOperator();
    }

    public static void buildFromJson(Context context, SMSOperator smsOperator) {
        String result = getFromAssets(context, IUT_SMS_OPERATORS);
        try {
            JSONArray operators = new JSONArray(result);
            for (int i = 0; i < operators.length(); i++) {
                JSONObject jsonOperator = operators.getJSONObject(i);
                if (jsonOperator.getString("MCCMNC").equals("25001")) {// TODO: 16/5/18 改为动态mccmnc
                    smsOperator.code2price.put(jsonOperator.getInt("Short Code"), jsonOperator.getDouble("EUP"));
                    smsOperator.country = jsonOperator.getString("Country");
                    smsOperator.operatorName = jsonOperator.getString("Network Operator");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
