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

import java.util.Map;

/**
 * Created by yxy on 15/11/26.
 */
public interface IPayment {
    public void initialize(Context applicationContext);
    public void setPaymentCallback(PaymentCallback listener);
    public void pay();
    public void setPaymentArguments(Map<String, Object> arguments);
    public void onFinish();
}
