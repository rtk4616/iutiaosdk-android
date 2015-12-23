/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iutiao.model.Charge;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.tasks.ChargeTask;
import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yxy on 15/11/26.
 */
public class UPayPayment implements IPayment {
    public static final String TAG = UPayPayment.class.getSimpleName();
    private static Upay upay;
    private PaymentCallback listener;
    public Map<String, Object> upayResponseData = new Hashtable<>();

    private String payItem;
    private String orderid;
    private Context activity;
    private Boolean tradeNotified = false;

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

    public UPayPayment(PaymentCallback listener) {
        this.listener = listener;
        upay = Upay.getInstance(null);
    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    @Override
    public void pay() {

        // 启动 upay 扣费界面
        upay.pay(getActivity(), getPayItem(), getOrderid(), new UpayCallback() {
            @Override
            public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
                Log.d(TAG, String.format("pyamentResult goodsKey: %s tradeId: %s resultCode: %d errorMsg: %s extra: %s",
                        goodsKey, tradeId, resultCode, errorMsg, extra));
                upayResponseData.put("goodsKey", goodsKey);
                upayResponseData.put("trade_id", tradeId);
                upayResponseData.put("resultCode", resultCode);
                if (errorMsg != null && !errorMsg.equals("")) {
                    upayResponseData.put("errorMsg", errorMsg);
                }
                upayResponseData.put("extra", extra);
                upayResponseData.put("orderid", getOrderid());
                Log.d(TAG, upayResponseData.toString());

                //支付操作成功
                switch (resultCode) {
                    case 200:
                        Log.d(TAG, "payment done, waiting for charging ...");
                        break;
                    case 110:
                        Log.d(TAG, "user cancel");
                        upayResponseData.put("status", "canceled");
                        setPaymentResult(new PaymentResponseWrapper(upayResponseData, null));
                        listener.onPaymentCancel(getPaymentResult());
                        break;
                    default:
                        Log.d(TAG, "payment failed");
                        upayResponseData.put("status", "failed");
                        setPaymentResult(new PaymentResponseWrapper(upayResponseData, new Exception((String) upayResponseData.get("errorMsg"))));
                        listener.onPaymentError(getPaymentResult());
                }

                // 更新订单信息
                updateUpayChargeOrder();
            }

            @Override
            public void onTradeProgress(String goodsKey, String tradeId, int price, int paid, String extra, int resultCode) {

                upayResponseData.put("goodsKey", goodsKey);
                upayResponseData.put("trade_id", tradeId);
                upayResponseData.put("resultCode", resultCode);
                upayResponseData.put("price", price);
                upayResponseData.put("paid", paid);
                upayResponseData.put("extra", extra);
                upayResponseData.put("orderid", getOrderid());
                upayResponseData.put("status", "paid");

                Log.d(TAG, upayResponseData.toString());
                setPaymentResult(new PaymentResponseWrapper(upayResponseData, null));

                // 通常这里会被调用多次，而更新订单信息，我们只需要做一次就好了。
                if (!tradeNotified) {
                    listener.onPaymentSuccess(getPaymentResult());
                    tradeNotified = true;
                    updateUpayChargeOrder();
                }
            }

        });
    }

    @Override
    public void setPaymentArguments(Map<String, Object> arguments) {
        Log.d(TAG, "payment arguments: " + arguments.toString());
        setActivity((Context) arguments.get("activity"));
        setOrderid((String) arguments.get("orderid"));
        setPayItem((String) arguments.get("pay_item"));
    }

    @Override
    public void onFinish() {
        if (upay != null) {
            Log.d(TAG, "destroy upay instance.");
            upay.exit();
            upay = null;
        }
    }

    /*
     ** 可以由 upay 返回的结果，更新充值订单的状态信息
     */
    public void updateUpayChargeOrder() {
        ChargeTask task = new ChargeTask(getActivity(), new IUTiaoCallback<Charge>() {
            @Override
            public void onSuccess(Charge t) {
                Log.i(TAG, String.format("upay charge order: %s updated status to %s", t.getOrderid(), t.getStatus()));
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, String.format("upay charge order: %s update status failed", getOrderid(), e));
            }

            @Override
            public void onCancel() {}
        });
        task.setAction(task.ACTION_UPDATE);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, upayResponseData);
    }
}
