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
import android.content.Intent;
import android.util.Log;

import com.iutiao.sdk.CallbackManagerImpl;
import com.iutiao.sdk.UserManager;
import com.payssion.android.sdk.PayssionActivity;
import com.payssion.android.sdk.model.PayRequest;
import com.payssion.android.sdk.model.PayResponse;

import java.util.Hashtable;
import java.util.Map;

public class PassionPayment implements IPayment {
    public static final String TAG = PassionPayment.class.getSimpleName();
    private PaymentCallback listener;
    public Map<String, Object> payssionResponseData = new Hashtable<>();

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private String payMethod;

    private String payItem;
    private String orderId;

    private double amount;
    private String currency;
    private String pmId;
    private String payerEmail = "yxy.870@gmail.com";
    private final String apiKey = "82222fa443ce357f";
    private final String secretKey = "ad3489a6b52b93dd75e9e1d6a3c23eea";

    private Context activity;

    public Context getActivity() {
        return activity;
    }

    public void setActivity(Context activity) {
        this.activity = activity;
    }

    private PaymentResponseWrapper paymentResult;

    public PaymentResponseWrapper getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResponseWrapper paymentResult) {
        this.paymentResult = paymentResult;
    }

    public PassionPayment() {

    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public void initialize(Context applicationContext) {

    }

    @Override
    public void setPaymentCallback(PaymentCallback listener) {
        this.listener = listener;
    }

    @Override
    public void pay(Activity context) {
            Log.i(TAG, String.format("Payment[passion]. trying to reload %s, orderid %s ", getAmount(), getOrderId()));
            Intent intent = new Intent(getActivity(), PayssionActivity.class);
            intent.putExtra(PayssionActivity.ACTION_REQUEST,
                    new PayRequest()
                            .setAPIKey(apiKey) //Payssion帐户API Key
                            .setAmount(amount) //订单金额
                            .setCurrency(currency) //货币
                            .setPMId(pmId) //支付方式id
                            .setPayerRef("other-ref") //支付方式的其他参数
                            .setDescription(amount/10+"U") //订单说明
                            .setTrackId(orderId)
                            .setSecretKey(secretKey) //Secret Key
                            .setPayerEmail(payerEmail) //付款人人邮箱
                            .setPayerName(UserManager.getInstance().getCurrentUser().getUid())); //付款人姓名

            CallbackManagerImpl.registerStaticCallback(0, new CallbackManagerImpl.Callback() {
                @Override
                public boolean onActivityResult(int resultCode, Intent data) {
                    Log.v(this.getClass().getSimpleName(), "onActivityResult");

                    switch (resultCode) {
                        case PayssionActivity.RESULT_OK:
                            if (null != data) {
                                PayResponse response = (PayResponse) data.getSerializableExtra(PayssionActivity.RESULT_DATA);
                                if (null != response) {
                                    //去服务端查询该笔订单状态，注意订单状态以服务端为准
                                    payssionResponseData.put("price", response.getAmount());
                                    payssionResponseData.put("status", "paid");
                                    Log.i(TAG, "-- payment done, waiting for charging ...");
                                } else {
                                    //should never go here
                                }
                            }
                            listener.onPaymentSuccess(new PaymentResponseWrapper(payssionResponseData,null));
                            break;
                        case PayssionActivity.RESULT_CANCELED:
                            Log.i(TAG, "user canceled");
                            payssionResponseData.put("status", "canceled");
                            //用户取消支付
                            listener.onPaymentCancel(new PaymentResponseWrapper(payssionResponseData,null));
                            break;
                        case PayssionActivity.RESULT_ERROR:
                            Log.i(TAG, "payment failed");
                            payssionResponseData.put("status", "failed");
                            //支付发生错误
                            if (null != data) {
                                //获取错误信息描述
                                String des = data.getStringExtra(PayssionActivity.RESULT_DESCRIPTION);
                                Log.v(this.getClass().getSimpleName(), "RESULT_ERROR" + des);
                                listener.onPaymentError(new PaymentResponseWrapper(payssionResponseData,new Exception(des)));
                            }
                            break;
                    }
                    return false;
                }
            });

            ((Activity) getActivity()).startActivityForResult(intent, 0);
    }


    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {
        Log.d(TAG, "payment arguments: " + arguments.toString());
        setActivity((Context) arguments.get("activity"));
        setOrderId((String) arguments.get("orderid"));
        setPayItem((String) arguments.get("pay_item"));
        setPayMethod((String) arguments.get("pay_method"));
        setAmount(Double.valueOf((String) arguments.get("amount")));
        setCurrency((String) arguments.get("currency"));
        pmId = ((String) arguments.get("pmid"));
    }

    @Override
    public void onFinish() {
    }
}
