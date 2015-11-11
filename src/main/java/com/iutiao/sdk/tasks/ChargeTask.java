/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.tasks;

import android.content.Context;

import com.iutiao.model.Charge;
import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

/**
 * Created by yxy on 15/11/11.
 */
public class ChargeTask extends IUTiaoRequestTask<Map<String, Object>, Charge> {
    public ChargeTask(Context context) {
        super(context);
    }

    public ChargeTask(Context context, IUTiaoCallback listener) {
        super(context, listener);
    }

    @Override
    protected Charge parse(Map<String, Object> stringObjectMap) throws Exception {
        return Charge.create(stringObjectMap);
    }
}
