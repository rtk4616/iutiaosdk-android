/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/5.
 */

public abstract class IUTiaoRequestTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    private ProgressDialog progressBar;
    private IUTiaoCallback listener;
    private Exception e;
    private Context context;
    private static final String TAG = IUTiaoRequestTask.class.getSimpleName();

    protected abstract Result parse(Params params) throws Exception;

    public IUTiaoRequestTask(Context context) {
        this(context, null);
    }

    public IUTiaoRequestTask(Context context, IUTiaoCallback listener) {
        this.listener = listener;
        this.context = context;
        this.progressBar = new ProgressDialog(context);
    }

    public void setListener(IUTiaoCallback listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgress();
    }

    private void showProgress() {
        this.progressBar.setCancelable(false);
        this.progressBar.setMessage(this.context.getString(R.string.com_iutiao_progress_loading));
        this.progressBar.show();
    }

    private void dismissProgress() {
        this.progressBar.dismiss();
    }

    @Override
    protected Result doInBackground(Params... params) {
        Result result = null;
        Params param = null;
        if (params.length > 0) {
            param = params[0];
        }
        try {
            result = parse(param);
        } catch (Exception e) {
            this.e = e;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        dismissProgress();
        if (e != null) {
            Log.e(TAG, "Request failed", e);
            this.listener.onError(e);
        } else {
            Log.i(TAG, "Request success");
            this.listener.onSuccess(result);
        }
    }

    @Override
    protected void onCancelled() {
        this.listener.onCancel();
    }

}