/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.tasks;

import android.content.Context;

import com.iutiao.model.OKEntity;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

public class ChangePasswordTask extends IUTiaoRequestTask<Map<String, Object>, OKEntity> {
    public ChangePasswordTask(Context context) {
        super(context);
    }

    public ChangePasswordTask(Context context, IUTiaoCallback listener) {
        super(context, listener);
    }

    @Override
    protected OKEntity parse(Map<String, Object> stringObjectMap) throws Exception {
        return User.changePassword(stringObjectMap);
    }
}
