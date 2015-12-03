/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import android.os.AsyncTask;

import com.iutiao.model.AppOrder;
import com.iutiao.model.UTiaoObject;
import com.iutiao.sdk.model.OrderInfo;

import java.util.concurrent.Executor;

/**
 * Created by yxy on 15/12/3.
 */
public class AppOrderManager {

    private static final String TAG = AppOrderManager.class.getSimpleName();

    public static class ResponseWrapper<T extends UTiaoObject> {
        public T model;
        public Exception exception;

        public ResponseWrapper(T model, Exception exception) {
            this.model = model;
            this.exception = exception;
        }
    }

    public interface Callback {
        void onSuccess(AppOrder order);
        void onError(Exception exception);
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
                } catch (Exception e) {
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
        } else if (result.exception != null) {
            callback.onError(result.exception);
        } else {
            callback.onError(new RuntimeException("Somehow we got unexpected error response."));
        }
    }

    public static void retrieve(String orderId, Executor executor, Callback callback) {

    }

    private static void executeTask(Executor executor, AsyncTask<Void, Void, ResponseWrapper> task) {
        if (executor != null) {
            task.executeOnExecutor(executor);
        } else {
            task.execute();
        }
    }

}
