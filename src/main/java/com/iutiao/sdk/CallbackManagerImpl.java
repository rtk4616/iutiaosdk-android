/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yxy on 15/11/4.
 */
public class CallbackManagerImpl implements CallbackManager {

    private static Map<Integer, Callback> staticCallbacks = new HashMap<>();

    public synchronized static void registerStaticCallback(
            int requestCode, Callback callback) {
        Validate.notNull(callback, "callback");
        if (staticCallbacks.containsKey(requestCode)) {
            return;
        }
        staticCallbacks.put(requestCode, callback);
    }

    private static synchronized Callback getStaticCallback(Integer requestCode) {
        return staticCallbacks.get(requestCode);
    }

    private static boolean runStaticCallback(int requestCode, int resultCode, Intent data) {
        Callback callback = getStaticCallback(requestCode);
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }
        return false;
    }

    public interface Callback {
        public boolean onActivityResult(int resultCode, Intent data);
    }

    // 实例保留一个回调集合
    private Map<Integer, Callback> callbacks = new HashMap<>();

    public void registerCallback(int requestCode, Callback callback) {
        Validate.notNull(callback, "callback");
        callbacks.put(requestCode, callback);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Callback callback = callbacks.get(requestCode);
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }

        return runStaticCallback(requestCode, resultCode, data);
    }

    public enum RequestCodeOffset {
        Login(0),
        Pay(1),
        ;
        private final int offset;

        RequestCodeOffset(int offset) {
            this.offset = offset;
        }

        public int toRequestCode() {
            return IUTiaoSdk.getCallbackRequestCodeOffset() + offset;
        }
    }
}
