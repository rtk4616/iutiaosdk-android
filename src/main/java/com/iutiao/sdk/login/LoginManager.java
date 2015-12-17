/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iutiao.exception.UTiaoException;
import com.iutiao.model.User;
import com.iutiao.net.RequestOptions;
import com.iutiao.sdk.AccessTokenManager;
import com.iutiao.sdk.CallbackManager;
import com.iutiao.sdk.CallbackManagerImpl;
import com.iutiao.sdk.IUTiaoActivity;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.Utility;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.exceptions.IUTiaoSdkException;
import com.iutiao.sdk.tasks.RegisterUserTask;
import com.iutiao.sdk.tasks.UserProfileTask;

import java.util.HashMap;
import java.util.logging.LogManager;

/**
 * Created by yxy on 15/11/6.
 */
public class LoginManager {

    private static volatile LoginManager instance;
    private static final String TAG = LoginManager.class.getSimpleName();

    public static void registerLoginCallback(
            final Activity activity,
            View loginClickable,
            final CallbackManager callbackManager,
            final IUTiaoCallback<User> callback) {
        getInstance().registerCallback(callbackManager, callback);
        loginClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, IUTiaoActivity.class);
                activity.startActivityForResult(i, CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
            }
        });

    }

    public void registerCallback(
            final CallbackManager callbackManager,
            final IUTiaoCallback<User> callback) {
        if (!(callbackManager instanceof CallbackManager)) {
            throw new IUTiaoSdkException("Unexpected CallbackManager, please use the provider Factory");
        }
        ((CallbackManagerImpl) callbackManager).registerCallback(
                CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode(),
                new CallbackManagerImpl.Callback() {
                    @Override
                    public boolean onActivityResult(int resultCode, Intent data) {
                        return LoginManager.this.onActivityResult(resultCode, data, callback);
                    }
                }
        );

    }

    LoginManager() {
        Validate.sdkInitialize();
    }

    public void logOut() {

        Log.i(TAG, "user logout");
        RequestOptions.getInstance().clearToken();
        AccessTokenManager.getInstance().clearCache();
        UserManager.getInstance().clearCache();
    }

    public static LoginManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LoginManager();
                }
            }
        }
        return instance;
    }

    private boolean onActivityResult(int resultCode, Intent data) {
        return onActivityResult(resultCode, data, null);
    }

    private boolean onActivityResult(int resultCode, Intent data, IUTiaoCallback<User> callback) {
        UTiaoException e;
        String token = null;
        User user = null;
        Exception exception = null;
        boolean isCanceled = false;
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra("error") != null) {
                    exception = new IUTiaoSdkException(data.getStringExtra("error"));
                } else if (data.getStringExtra("user") != null) {
                    user = User.GSON.fromJson(data.getStringExtra("user"), User.class);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            isCanceled = true;
        }

        finishLogin(user, exception, isCanceled, callback);
        return true;
    }

    private void finishLogin(User user, Exception exception, boolean isCanceled, IUTiaoCallback<User> callback) {
        if (callback != null) {
            if (isCanceled) {
                Log.d(TAG, "login canceled");
                callback.onCancel();
            } else if (exception != null) {
                Log.e(TAG, "login failed", exception);
                callback.onError(exception);
            } else if (user != null) {
                Log.i(TAG, "login succeed");
                callback.onSuccess(user);
            }
        }
    }

    public void onLogin(User user) {

        Log.i(TAG, "onLogin user " + user);
        Validate.notNull(user.getToken(), "access token");
        // 缓存用户信息
        UserManager.getInstance().setCurrentUser(user);
        // 缓存 access_token
        AccessTokenManager.getInstance().setCurrentAccessToken(user.getToken());
        // 设置 client 请求的 token
        RequestOptions.getInstance().setToken(user.getToken());
    }

    public static void silentLogin(final IUTiaoCallback<User> callback) {
        if (UserManager.getInstance().hasCache()) {
            Log.d(TAG, "use cached user profile");
            User user = UserManager.getInstance().loadProfile();
            user.setToken(AccessTokenManager.getInstance().getCurrentAccessToken());
            LoginManager.getInstance().onLogin(user);
            callback.onSuccess(user);
        } else {
            Log.i(TAG, "Create a guest");
            UserManager.createGuest(new UserManager.Callback() {

                @Override
                public void onSuccess(User user) {
                    LoginManager.getInstance().onLogin(user);
                    callback.onSuccess(user);
                }

                @Override
                public void onError(UTiaoException e) {
                    callback.onError(e);
                }
            });
        }
    }
}
