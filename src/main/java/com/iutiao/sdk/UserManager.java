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

import com.iutiao.model.User;
import com.iutiao.sdk.util.CacheSharedPreference;

/**
 * Created by yxy on 15/11/4.
 */
public final class UserManager extends CacheSharedPreference {

    private User currentUser;
    private final String CACHED_PROFILE_KEY =
            "com.iutiao.UserManager.CachedUserProfile";
    static final String SHARED_PREFERENCES_NAME =
            "com.iutiao.UserManager.SharedPreferences";
    private static UserManager instance;

    @Override
    protected String getCacheKey() {
        return this.CACHED_PROFILE_KEY;
    }

    public UserManager() {
        super(IUTiaoSdk.getApplicationContext()
                .getSharedPreferences(UserManager.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = loadProfile();
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        cacheUserProfile(currentUser);
    }

    public User loadProfile() {
        return User.GSON.fromJson(getCachedContent(), User.class);
    }

    public void cacheUserProfile(User user) {
        Validate.notNull(user, "user");
        cache(User.GSON.toJson(user));
    }

    public boolean hasCacahedUserProfile() {
        return hasCache();
    }

}
