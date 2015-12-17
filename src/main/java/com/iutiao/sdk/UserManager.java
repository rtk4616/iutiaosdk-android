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
import android.os.AsyncTask;
import android.util.Log;

import com.iutiao.exception.APIConnectionException;
import com.iutiao.exception.APIException;
import com.iutiao.exception.UTiaoException;
import com.iutiao.model.AppOrder;
import com.iutiao.model.User;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.util.CacheSharedPreference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

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
    final static String TAG = UserManager.class.getSimpleName();

    public interface Callback {
        void onSuccess(User user);
        void onError(UTiaoException e);
    }

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

    public static void create(Map<String, Object> params, final Callback callback) {
        create(params, null, callback);
    }

    public static void create(final Map<String, Object> params, Executor executor, final Callback callback) {

        AsyncTask<Void, Void, ResponseWrapper> task = new AsyncTask<Void, Void, ResponseWrapper>() {
            @Override
            protected ResponseWrapper doInBackground(Void... args) {
                try {
                    User user = User.create(params);
                    return new ResponseWrapper(user, null);
                } catch (UTiaoException e) {
                    return new ResponseWrapper(null, e);
                } catch (Exception e) {
                    Log.e(TAG, "create user failed", e);
                    return new ResponseWrapper(null, new APIException(e.getMessage(), null, 800, e));
                }
            }

            @Override
            protected void onPostExecute(ResponseWrapper result) {
                taskPostExecution(result, callback);
            }
        };
        executeTask(executor, task);
    }

    private static void executeTask(Executor executor, AsyncTask<Void, Void, ResponseWrapper> task) {
        if (executor != null) {
            task.executeOnExecutor(executor);
        } else {
            task.execute();
        }
    }

    private static void taskPostExecution(ResponseWrapper result, Callback callback) {
        if (result.model != null) {
            User user = (User) result.model;
            callback.onSuccess(user);
        } else {
            callback.onError(result.exception);
        }
    }

    public static void createGuest(final Callback callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", Utility.generateRandomString(16));
        params.put("password", Utility.generateRandomString(16));
        params.put("is_guest", true);
        create(params, callback);
    }
}
