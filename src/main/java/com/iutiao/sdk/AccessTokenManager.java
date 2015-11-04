/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import android.content.Context;

/**
 * Created by yxy on 15/11/4.
 */
public final class AccessTokenManager {

    static final String SHARED_PREFERENCES_NAME =
            "com.iutiao.AccessTokenManager.SharedPreferences";
    private final AccessTokenCache accessTokenCache;
    private String currentAccessToken;
    private static volatile AccessTokenManager instance;

    AccessTokenManager(AccessTokenCache accessTokenCache) {
        Validate.notNull(accessTokenCache, "accessTokenCache");
        this.accessTokenCache = accessTokenCache;
    }

    static AccessTokenManager getInstance() {
        if (instance == null) {
            synchronized (AccessTokenManager.class) {
                if (instance == null) {
//                    Context applicationContext = IUTiaoSdk.getApplicationContext();
                    AccessTokenCache accessTokenCache = new AccessTokenCache();
                    instance = new AccessTokenManager(accessTokenCache);
                }
            }
        }
        return instance;
    }
    
    void setCurrentAccessToken(String currentAccessToken) {
        setCurrentAccessToken(currentAccessToken, true);
    }

    public String getCurrentAccessToken() {
        return currentAccessToken;
    }

    private void setCurrentAccessToken(String currentAccessToken, boolean saveToCache) {
        String oldAccessToken = this.currentAccessToken;
        this.currentAccessToken = currentAccessToken;

        if (saveToCache) {
            if (currentAccessToken != null) {
                accessTokenCache.save(currentAccessToken);
            } else {
                // clear access token if input null
                accessTokenCache.clear();
            }
        }
    }
}
