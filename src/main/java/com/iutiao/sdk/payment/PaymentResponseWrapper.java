/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.payment;

import java.util.Map;

/**
 * Created by yxy on 15/11/26.
 */
public class PaymentResponseWrapper {

    public final Map<String, Object> data;
    public final Exception error;

    public PaymentResponseWrapper(Map<String, Object> data, Exception error) {
        this.data = data;
        this.error = error;
    }
}
