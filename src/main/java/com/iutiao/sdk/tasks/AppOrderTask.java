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

import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

/**
 * Created by yxy on 15/11/13.
 */
public class AppOrderTask extends IUTiaoRequestTask<Map<String, Object>, com.iutiao.model.AppOrder> {

    String action;

    public AppOrderTask(Context context) {
        super(context);
    }

    public AppOrderTask(Context context, IUTiaoCallback listener, String action) {
        super(context, listener);
        this.action = action;
    }

    @Override
    protected com.iutiao.model.AppOrder parse(Map<String, Object> stringObjectMap) throws Exception {

        if (action == "create") {
            return com.iutiao.model.AppOrder.create(stringObjectMap);
        } else if (action == "retrieve") {
            return com.iutiao.model.AppOrder.retrieve((String) stringObjectMap.get("orderid"));
        }
        return null;
    }
}
