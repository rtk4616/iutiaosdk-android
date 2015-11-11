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

/**
 * Created by yxy on 15/11/11.
 */
public class ChargeFragment extends BaseFragment implements IUTiaoCallback<Charge>, View.OnClickListener {

    private Button confirmBtn;
    private EditText amountET;
    private static final String TAG = ChargeFragment.class.getSimpleName();

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        confirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        amountET = (EditText) view.findViewById(R.id.et_amount);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChargeTask task = new ChargeTask(getActivity(), (IUTiaoCallback<Charge>) ChargeFragment.this);
                HashMap<String, Object> params = new HashMap<>();
                params.put("pay_method", getPayMethod());
                params.put("currency", getCurrency());
                params.put("pay_item", "5b");
                task.execute(params);
            }
        });
        
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
        Upay up = Upay.getInstance(null);
        String goodskey = "rub_35";
        String extra = charge.getOrderid();
        up.pay(getActivity(), goodskey, extra, new UpayCallback() {
            @Override
            public void onPaymentResult(String s, String s1, int i, String s2, String s3) {
                Log.i(TAG, "payment results " + s + s1 + i + s2 + s3);
            }

            @Override
            public void onTradeProgress(String s, String s1, int i, int i1, String s2, int i2) {
                Log.i(TAG, "payment tradeProgress " + s + s1 + i + s2 + i2);
            }
        });
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {

    }
}
