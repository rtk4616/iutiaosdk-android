/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iutiao.model.Charge;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoSdk;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.tasks.ChargeTask;
import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yxy on 15/11/11.
 */
public class ChargeFragment extends BaseFragment implements IUTiaoCallback<Charge>, View.OnClickListener {

    private Button confirmBtn;
    private EditText amountET;
    private static final String TAG = ChargeFragment.class.getSimpleName();
    private String payItem = "rub_35";
    private String orderid;
    private String tradeId;

    private static Upay upay;

    public static ChargeFragment newInstance() {
        return new ChargeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_charge, container, false);
    }

    private String getAmount() {
        return amountET.getText().toString().trim();
    }

    private void setAmount(String amount) {
        amountET.setText(amount);
    }

    private String getPayMethod() {
        return "upay";
    }

    private String getCurrency() {
        return IUTiaoSdk.getCurrency();
    }

    public String newOrderid() {
        setOrderid(UUID.randomUUID().toString());
        return getOrderid();
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        upay = Upay.getInstance(null);

        confirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        amountET = (EditText) view.findViewById(R.id.et_amount);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayItem("rub_35");

                createChargeOrder();

            }
        });
        
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public void createChargeOrder() {
        ChargeTask task = new ChargeTask(getActivity(), (IUTiaoCallback<Charge>) ChargeFragment.this);
        HashMap<String, Object> params = new HashMap<>();
        params.put("pay_method", getPayMethod());
        params.put("currency", getCurrency());
        params.put("trade_id", getTradeId());
        params.put("orderid", newOrderid());
        params.put("pay_item", getPayItem());
        task.execute(params);
    }

    public void updateChargeOrder(Map<String, Object> params) {
        ChargeTask task = new ChargeTask(getActivity(), new IUTiaoCallback() {
            @Override
            public void onSuccess(Object t) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancel() {

            }
        });
        task.setAction(task.ACTION_UPDATE);
        task.execute(params);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_1u) {
            setAmount("1");
        } else if (id == R.id.btn_10u) {
            setAmount("10");
        } else if (id == R.id.btn_5u) {
            setAmount("5");
        }
    }

    @Override
    public void onSuccess(Charge charge) {
        UPayPayment payment = new UPayPayment();
        payment.pay();
    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    public interface PaymentCallback {
        void onSuccess(Map<String, Object> data);
        void onError(Map<String, Object> data);
        void onCancel(Map<String, Object> data);
    }

    private class UPayPayment implements PaymentCallback {

        PaymentCallback listener;

        public UPayPayment() {
            listener = this;
        }

        @Override
        public void onSuccess(Map<String, Object> data) {
            // 查询订单到账通知
            // 结束当前 activity
            updateChargeOrder(data);
//            getActivity().setResult();
            getActivity().finish();
        }

        @Override
        public void onError(Map<String, Object> data) {
            // 订单出错
            updateChargeOrder(data);
        }

        @Override
        public void onCancel(Map<String, Object> data) {
            // 取消订单
            updateChargeOrder(data);
        }

        public Map<String, Object> upayResponseData = new Hashtable<>();

        public void pay() {

            // 启动 upay 扣费界面
            upay.pay(getActivity(), getPayItem(), getOrderid(), new UpayCallback() {
                @Override
                public void onPaymentResult(String goodsKey, String tradeId, int resultCode, String errorMsg, String extra) {
                    Log.i(TAG, String.format("pyamentResult goodsKey: %s tradeId: %s resultCode: %d errorMsg: %s extra: %s",
                            goodsKey, tradeId, resultCode, errorMsg, extra));
                    upayResponseData.put("goodsKey", goodsKey);
                    upayResponseData.put("trade_id", tradeId);
                    upayResponseData.put("resultCode", resultCode);
                    if (errorMsg != null && !errorMsg.equals("")) {
                        upayResponseData.put("errorMsg", errorMsg);
                    }
                    upayResponseData.put("extra", extra);
                    upayResponseData.put("orderid", getOrderid());
                    Log.i(TAG, upayResponseData.toString());

                    //支付操作成功
                    switch (resultCode) {
                        case 200:
                            Log.i(TAG, "payment done, waiting for charging ...");
                            break;
                        case 110:
                            Log.i(TAG, "user cancel");
                            upayResponseData.put("status", "canceled");
                            listener.onCancel(upayResponseData);
                            break;
                        default:
                            Log.i(TAG, "payment failed");
                            upayResponseData.put("status", "failed");
                            listener.onError(upayResponseData);
                    }

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

                    Log.i(TAG, upayResponseData.toString());

                    listener.onSuccess(upayResponseData);
                }

            });
        }
    }



    @Override
    public void onError(Exception e) {
        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onDestroy() {
        upay.exit();
        upay = null;
        super.onDestroy();
    }
}
