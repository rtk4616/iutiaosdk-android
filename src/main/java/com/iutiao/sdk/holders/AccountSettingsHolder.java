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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.IUTTitleBar;

public class AccountSettingsHolder extends IUTViewHolder {
    public LinearLayout resetPwdLL;
    public LinearLayout bindPhoneLL;
    public TextView phoneBindStateTv;
    public TextView emailBindStateTv;
    public TextView uidTv;
    public TextView nicknameTv;
    public TextView balanceTv;
    public IUTTitleBar title;
    public LinearLayout bindEmailLL;

    private AccountSettingsHolder(Context context, View view) {
        super(context, view);
    }

    public static AccountSettingsHolder Create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        AccountSettingsHolder profileHolder = new AccountSettingsHolder(context,inflater.inflate(R.layout.com_iutiao_fragment_account_settings, null, false));
        return profileHolder;
    }

    private void findViews(View root) {
        phoneBindStateTv = (TextView) root.findViewById(R.id.tv_phone_bind_state);
        emailBindStateTv = (TextView) root.findViewById(R.id.tv_email_bind_state);
        uidTv= (TextView) root.findViewById(R.id.tv_uid);
        nicknameTv = (TextView) root.findViewById(R.id.tv_nickname);
        balanceTv = (TextView) root.findViewById(R.id.tv_balance);
        resetPwdLL = (LinearLayout) root.findViewById(R.id.ll_reset_pwd);
        bindPhoneLL = (LinearLayout) root.findViewById(R.id.ll_bind_phone);
        bindEmailLL = (LinearLayout) root.findViewById(R.id.ll_bind_email);
        title = (IUTTitleBar) root.findViewById(R.id.iuttb_title);
    }

    @Override
    void onHolderCreate(View root) {
        findViews(root);
    }

    @Override
    public void showError(String errMsg) {
        Toast.makeText(context,errMsg,Toast.LENGTH_SHORT);
    }

    @Override
    public void hideError() {

    }
}