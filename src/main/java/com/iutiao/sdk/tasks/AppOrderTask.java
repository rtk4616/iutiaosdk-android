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
import com.iutiao.sdk.R;

import java.util.Map;

/**
 * Created by yxy on 15/11/13.
 */
public class AppOrderTask extends IUTiaoRequestTask<Map<String, Object>, com.iutiao.model.AppOrder> {

    String action = "create";

    public AppOrderTask(Context context) {
        super(context);
    }

    public AppOrderTask(Context context, IUTiaoCallback listener, String action) {
        super(context, listener);
        this.action = action;
    }

    @Override
    protected int getProgressMessage() {
        if (action == "create") {
            return R.string.com_iutiao_progress_loading_create_app_order;
        } else if (action == "retrieve") {
            return R.string.com_iutiao_progress_loading_retrieve_app_order;
        }
        return super.getProgressMessage();
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
