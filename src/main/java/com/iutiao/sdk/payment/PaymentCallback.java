/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment;

/**
 * Created by yxy on 15/11/26.
 */
public interface PaymentCallback {
    void onPaymentSuccess(PaymentResponseWrapper result);
    void onPaymentError(PaymentResponseWrapper result);
    void onPaymentCancel(PaymentResponseWrapper result);
}
