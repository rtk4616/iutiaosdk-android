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
import android.view.View;
import android.widget.Button;

import com.iutiao.exception.UTiaoException;
import com.iutiao.model.User;
import com.iutiao.sdk.AccessTokenManager;
import com.iutiao.sdk.CallbackManager;
import com.iutiao.sdk.CallbackManagerImpl;
import com.iutiao.sdk.IUTiaoActivity;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.exceptions.IUTiaoSdkException;

import java.util.logging.LogManager;

/**
 * Created by yxy on 15/11/6.
 */
public class LoginManager {

    private static volatile LoginManager instance;

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
        AccessTokenManager.getInstance().setCurrentAccessToken(null);
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
            user = User.GSON.fromJson(data.getStringExtra("user"), User.class);
            if (user != null) {
                if (resultCode == Activity.RESULT_OK) {
                    token = user.getToken();
                    AccessTokenManager.getInstance().setCurrentAccessToken(token);
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
                callback.onCancel();
            } else if (exception != null) {
                callback.onError(exception);
            } else if (user != null) {
                callback.onSuccess(user);
            }
        }
    }
}
