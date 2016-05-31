/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.status;

import android.content.Context;

import java.util.Map;

public interface CrashLogedListener {
    /** 
     * 异常参数
     *  
     * @param
     */  
    public void afterSaveCrash(Context mContext, Map<String, String> crashInfos);
}  