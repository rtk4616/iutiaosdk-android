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

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

public class UpdateUserTask extends IUTiaoRequestTask<Map<String,Object>,User> {
    public UpdateUserTask(Context context) {
        super(context);
    }

    public UpdateUserTask(Context context, IUTiaoCallback listener) {
        super(context, listener);
    }

    @Override
    protected User parse(Map<String, Object> params) throws Exception {
        return User.update(params.get("id").toString(), params);
    }
}
