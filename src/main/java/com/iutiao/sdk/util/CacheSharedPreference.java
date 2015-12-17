/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.util;

import android.content.SharedPreferences;

import com.iutiao.sdk.Validate;

/**
 * Created by yxy on 15/12/3.
 */
public abstract class CacheSharedPreference {
    private final SharedPreferences sharedPreferences;

    protected abstract String getCacheKey();

    public CacheSharedPreference(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void cache(String obj) {
        this.sharedPreferences.edit().putString(getCacheKey(), obj).apply();
    }

    public String getCachedContent() {
        return this.sharedPreferences.getString(getCacheKey(), null);
    }

    public boolean hasCache() {
        return this.sharedPreferences.contains(getCacheKey());
    }

    public void clearCache() {
        this.sharedPreferences.edit().remove(getCacheKey()).apply();
    }
}
