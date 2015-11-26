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
import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.payment.UPayPayment;
import com.iutiao.sdk.tasks.ChargeTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yxy on 15/11/11.
 */
public class ChargeFragment extends BaseFragment implements PaymentCallback, View.OnClickListener {

    private Button confirmBtn;
    private EditText amountET;
    private static final String TAG = ChargeFragment.class.getSimpleName();
    private String payItem = "rub_35";
    private String orderid;
    private IPayment payment;

    private Map<String, Object> paymentArguments = new HashMap<>();

    public Map<String, Object> getPaymentArguments() {
        return paymentArguments;
    }

    public void setPaymentArguments(Map<String, Object> paymentArguments) {
        this.paymentArguments = paymentArguments;
    }

    public void initPaymentArguments() {
        this.paymentArguments.put("activity", getActivity());
        this.paymentArguments.put("currency", getCurrency());
        this.paymentArguments.put("pay_method", getPayMethod());
        this.paymentArguments.put("orderid", newOrderid());
        this.paymentArguments.put("amount", getAmount());
        if (getPayItem() != null && !getPayItem().equals("")) {
            this.paymentArguments.put("pay_item", getPayItem());
        }
    }

    public enum PAY_METHODS {
        upay,
    }

    public static ChargeFragment newInstance() {
        return new ChargeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_charge, container, false);
    }

    private String getAmount() {
        String amount = amountET.getText().toString().trim();
        if (amount.equals("")) {
            amount = "0";
        }
        return amount;
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

        confirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        amountET = (EditText) view.findViewById(R.id.et_amount);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayItem("rub_35");
                initPaymentArguments();
                createChargeOrder();
            }
        });
        
    }

    public void createChargeOrder() {
        ChargeTask task = new ChargeTask(getActivity(), new IUTiaoCallback<Charge>() {
            @Override
            public void onSuccess(Charge t) {
                if (getPayMethod().equals("upay")) {
                    payment = new UPayPayment((PaymentCallback) ChargeFragment.this);
                    paymentArguments.put("payitem", getPayItem());
                    payment.setPaymentArguments(getPaymentArguments());
                }
                payment.pay();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {

            }
        });

        task.execute(paymentArguments);
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

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    @Override
    public void onDestroy() {
        if (payment != null) {
            payment.onDestroy();
        }
        payment = null;
        super.onDestroy();
    }


    @Override
    public void onPaymentSuccess(PaymentResponseWrapper result) {
        Log.i(TAG, "payment success");
        Toast.makeText(getActivity(), "payment succeed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(PaymentResponseWrapper result) {
        Toast.makeText(getActivity(), "payment failed due to " + result.error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentCancel(PaymentResponseWrapper result) {
        Toast.makeText(getActivity(), "payment canceled", Toast.LENGTH_SHORT).show();
    }
}
