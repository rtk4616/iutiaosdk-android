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

import com.iutiao.model.UPayItem;
import com.iutiao.model.UPayItemCollection;
import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

/**
 * Created by yxy on 15/12/22.
 */
public class UPayItemCollectionTask extends IUTiaoRequestTask<Map<String, Object>, com.iutiao.model.UPayItemCollection> {
    public UPayItemCollectionTask(Context context) {
        super(context);
    }

    public UPayItemCollectionTask(Context context, IUTiaoCallback listener) {
        super(context, listener);
    }

    @Override
    protected UPayItemCollection parse(Map<String, Object> stringObjectMap) throws Exception {
        return UPayItem.all();
    }
}
