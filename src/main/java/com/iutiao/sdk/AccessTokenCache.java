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
import android.content.SharedPreferences;

/**
 * Created by yxy on 15/11/3.
 */
public final class AccessTokenCache {
    static final String CACHED_ACCESS_TOKEN_KEY
            = "com.iutiao.AccessTokenManager.CachedAccessToken";
    private final SharedPreferences sharedPreferences;

    AccessTokenCache(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public AccessTokenCache() {
        this(IUTiaoSdk.getApplicationContext().getSharedPreferences(
            AccessTokenManager.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        );
    }

    public String load() {
        return getCachedAccessToken();
    }

    public void save(String accessToken) {
        Validate.notNull(accessToken, "accessToken");
        sharedPreferences.edit().putString(CACHED_ACCESS_TOKEN_KEY, accessToken).apply();
    }

    public void clear() {
        sharedPreferences.edit().remove(CACHED_ACCESS_TOKEN_KEY).apply();
    }

    public boolean hasCachedAccessToken() {
        return sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY);
    }

    private String getCachedAccessToken() {
        return sharedPreferences.getString(CACHED_ACCESS_TOKEN_KEY, null);
    }
}
