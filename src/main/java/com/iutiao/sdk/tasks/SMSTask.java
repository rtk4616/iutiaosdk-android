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
import android.os.AsyncTask;

import com.iutiao.IUTiao;
import com.iutiao.model.OKEntity;
import com.iutiao.model.Sms;
import com.iutiao.sdk.IUTiaoCallback;

import java.util.Map;

/**
 * Created by yxy on 15/11/6.
 */
public class SMSTask extends IUTiaoRequestTask<Map<String, Object>, OKEntity> {

    public SMSTask(Context context) {
        super(context);
    }

    public SMSTask(Context context, IUTiaoCallback listener) {
        super(context, listener);
    }

    @Override
    protected OKEntity parse(Map<String, Object> stringObjectMap) throws Exception {
        String action = (String) stringObjectMap.get("action");
        String receiver = (String) stringObjectMap.get("receiver");
        switch (action) {
            case "register":
                return Sms.register(receiver);
            case "bind_phone":
                return Sms.bindPhone(receiver);
            case "obtain_token":
                return Sms.obtainToken(receiver);
            case "reset_password":
                return Sms.resetPassword(receiver);
            default:
                return null;
        }
    }
}
