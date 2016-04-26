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
import com.iutiao.sdk.views.IUTTitleBar;

public class BindEmailHolder extends IUTViewHolder {
    public EditText pwd1Et;
    public EditText pwd2Et;
    public TextView confirmBtn;
    public TextView errorTv;
    public IUTTitleBar title;

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

    public String getInputPwd1() {
        return pwd1Et.getText().toString().trim();
    }

    public String getInputPwd2() {
        return pwd2Et.getText().toString().trim();
    }

    private void findView(View root) {
        pwd1Et = (EditText) root.findViewById(R.id.et_pwd1);
        pwd2Et = (EditText) root.findViewById(R.id.et_pwd2);
        confirmBtn = (TextView) root.findViewById(R.id.btn_confirm);
        errorTv = (TextView) root.findViewById(R.id.tv_reset_pwd_error);
        title = (IUTTitleBar) root.findViewById(R.id.iuttb_title);
    }
}