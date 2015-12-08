/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
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
 * 充值U币
 *
 */
public class ChargeFragment extends BaseFragment implements PaymentCallback, View.OnClickListener {

    private static final String TAG = ChargeFragment.class.getSimpleName();

    private String payItem;
    private String orderid;
    private IPayment payment;
    private String payMethod;
    private String defaultPayMethod = "upay";

    // ui related
    private RadioGroup payMethodRG;
    private Button paymentBtn;
    private TextView amount;

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public enum PAY_METHODS {
        upay,
    }

    private Map<String, Object> paymentArguments = new HashMap<>();

    public Map<String, Object> getPaymentArguments() {
        return paymentArguments;
    }

    public void initPaymentArguments() {
        this.paymentArguments.put("activity", getActivity());
        this.paymentArguments.put("currency", getCurrency());

        String paymethod = getPayMethod();
        this.paymentArguments.put("pay_method", paymethod);
        this.paymentArguments.put("orderid", newOrderid());
        this.paymentArguments.put("amount", getAmount());
        if (paymethod.equals("upay") && getPayItem() != null && !getPayItem().equals("")) {
            this.paymentArguments.put("pay_item", getPayItem());
        }
    }

    public static ChargeFragment newInstance() {
        return new ChargeFragment();
    }

    public static ChargeFragment newInstance(Intent intent) {
        ChargeFragment fragment = newInstance();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_charge, container, false);
    }

    private String getAmount() {
        String amount = this.amount.getText().toString().trim();
        if (chooseUpay()) {
            amount = "0";
        }
        return amount;
    }

    private void setAmount(String amount) {
        this.amount.setText(amount);
    }

    private String getPayMethod() {
        return payMethod;
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

        setPayMethod(defaultPayMethod);

        paymentBtn = (Button) view.findViewById(R.id.btn_payment);
        amount = (TextView) view.findViewById(R.id.et_amount);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPaymentArguments();
                createChargeOrder();
            }
        });

        payMethodRG = (RadioGroup) view.findViewById(R.id.payment_method);

        payMethodRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.paymethod_upay) {
                    setPayMethod("upay");
                }
            }
        });

        view.findViewById(R.id.btn_2u).setOnClickListener(this);

    }

    public void createChargeOrder() {
        ChargeTask task = new ChargeTask(getActivity(), new IUTiaoCallback<Charge>() {
            @Override
            public void onSuccess(Charge t) {
                if (chooseUpay()) {
                    payment = new UPayPayment((PaymentCallback) ChargeFragment.this);
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

    private boolean chooseUpay() {
        return getPayMethod().equals("upay");
    }

    @Override
    public void onClick(View v) {
        if (chooseUpay()) {
            payItem = ((Button) v).getText().toString();
        }
        amount.setText(((Button) v).getText().toString());
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
