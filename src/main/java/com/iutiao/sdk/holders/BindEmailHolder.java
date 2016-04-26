/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.holders;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iutiao.sdk.R;

public class BindEmailHolder extends IUTViewHolder {
    public EditText emailEt;
    public TextView confirmBtn;
    public TextView errorTv;

    public BindEmailHolder(Context context, View root) {
        super(context, root);
    }

    @Override
    void onHolderCreate(View root) {
        findView(root);
    }

    @Override
    public void showError(String errMsg) {
        errorTv.setVisibility(View.VISIBLE);
        errorTv.setText(errMsg);
    }

    @Override
    public void hideError() {
        errorTv.setVisibility(View.GONE);
    }

    public String getInputEmail() {
        return emailEt.getText().toString().trim();
    }

    private void findView(View root) {
        emailEt = (EditText) root.findViewById(R.id.et_email_bind_email);
        confirmBtn = (TextView) root.findViewById(R.id.btn_email_bind_confirm);
        errorTv = (TextView) root.findViewById(R.id.tv_email_bind_error);
    }
}