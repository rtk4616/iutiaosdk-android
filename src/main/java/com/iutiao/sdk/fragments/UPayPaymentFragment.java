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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iutiao.sdk.R;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;

/**
 * Created by yxy on 15/12/21.
 */
public class UPayPaymentFragment extends BaseFragment implements PaymentCallback, View.OnClickListener {
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPaymentSuccess(PaymentResponseWrapper result) {

    }

    @Override
    public void onPaymentError(PaymentResponseWrapper result) {

    }

    @Override
    public void onPaymentCancel(PaymentResponseWrapper result) {

    }

    public static UPayPaymentFragment newInstance() {
        return new UPayPaymentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_payment_upay, container, false);
    }

}
