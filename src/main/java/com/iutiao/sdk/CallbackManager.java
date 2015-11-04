/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

/**
 * Created by yxy on 15/11/4.
 */

import android.content.Intent;

/**
 * CallbackManager 管理所有来自 Activity 或 Fragment 的 onActivityResult() 回调方法
 */
public interface CallbackManager {

    /**
     * 这个方法只能从 Activity 或 Fragment 的 onActivityResult 方法中调用
     *
     * @param requestCode Activity 或 Fragment 接收的请求代码
     * @param resultCode Activity 或 Fragment 接收的结果代码
     * @param data Activity 或 Fragment 接收的结果数据
     * @return 处理完返回 true
     */

    public boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 工厂方法
     */
    public static class Factory {
        // create an instance of {@link com.iutiao.sdk.CallbackManager}
        public static CallbackManager create() {
            return new CallbackManagerImpl();
        }
    }


}
