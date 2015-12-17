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

import com.iutiao.exception.APIConnectionException;
import com.iutiao.exception.UTiaoException;
import com.iutiao.model.AppOrder;
import com.iutiao.model.UTiaoObject;
import com.iutiao.sdk.model.OrderInfo;
import com.iutiao.sdk.util.CacheSharedPreference;

import java.util.concurrent.Executor;

/**
 * Created by yxy on 15/12/3.
 *
 */
public class AppOrderManager extends CacheSharedPreference {

    private static final String TAG = AppOrderManager.class.getSimpleName();
    private static AppOrderManager instance;
    private final String CACHED_KEY =
            "com.iutiao.AppOrderManager.CachedAppOrder";
    static final String SHARED_PREFERENCES_NAME =
            "com.iutiao.AppOrderManager.SharedPreferences";

    @Override
    protected String getCacheKey() {
        return this.CACHED_KEY;
    }

    public AppOrderManager() {
        super(IUTiaoSdk.getApplicationContext()
                .getSharedPreferences(AppOrderManager.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));
    }

    public static AppOrderManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new AppOrderManager();
                }
            }
        }
        return instance;
    }

    public interface Callback {
        void onSuccess(AppOrder order);
        void onError(UTiaoException exception);
    }

    public static void create(OrderInfo orderInfo, final Callback callback) {
        create(orderInfo, null, callback);
    }

    public static void create(final OrderInfo orderInfo, Executor executor, final Callback callback) {
        AsyncTask<Void, Void, ResponseWrapper> task = new AsyncTask<Void, Void, ResponseWrapper>() {
            @Override
            protected ResponseWrapper doInBackground(Void... params) {
                try {
                    AppOrder order = AppOrder.create(orderInfo.toHashMap());
                    return new ResponseWrapper(order, null);
                } catch (UTiaoException e) {
                    return new ResponseWrapper(null, e);
                }

            }

            @Override
            protected void onPostExecute(ResponseWrapper result) {
                taskPostExecution(result, callback);
            }
        };
        executeTask(executor, task);
    }

    private static void taskPostExecution(ResponseWrapper result, Callback callback) {
        if (result.model != null) {
            callback.onSuccess((AppOrder) result.model);
        } else {
            callback.onError(result.exception);
        }
    }

    public static void retrieve(final String orderId, final Callback callback) {
        retrieve(orderId, null, callback);
    }

    public static void retrieve(final String orderId, Executor executor, final Callback callback) {
        AsyncTask<Void, Void, ResponseWrapper> task = new AsyncTask<Void, Void, ResponseWrapper>() {
            @Override
            protected ResponseWrapper doInBackground(Void... params) {
                try {
                    AppOrder order = AppOrder.retrieve(orderId);
                    return new ResponseWrapper(order, null);
                } catch (UTiaoException e) {
                    return new ResponseWrapper(null, e);
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

    public static void cacheAppOrder(AppOrder appOrder) {
        Validate.notNull(appOrder, "appOrder");
        AppOrderManager.getInstance().cache(AppOrder.GSON.toJson(appOrder));
    }

    public static AppOrder loadCachedAppOrder() {
        return AppOrder.GSON.fromJson(getCachedAppOrder(), AppOrder.class);
    }

    public static String getCachedAppOrder() {
        return getInstance().getCachedContent();
    }

}
