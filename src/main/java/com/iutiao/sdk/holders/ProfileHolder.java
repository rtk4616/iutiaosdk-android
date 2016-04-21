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
import android.widget.ImageView;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.IUTTitleBar;

public class ProfileHolder extends IUTViewHolder {
    public IUTTitleBar titleBar;
    public TextView chargeBtn;
    public TextView balanceTv;
    public ImageView adsIv;

    private ProfileHolder(Context context, View view) {
        super(context, view);
    }

    public static ProfileHolder Create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ProfileHolder profileHolder = new ProfileHolder(context,inflater.inflate(R.layout.com_iutiao_fragment_profile, null, false));
        return profileHolder;
    }

    private void findViews(View root) {
        titleBar = (IUTTitleBar) root.findViewById(R.id.title);
        balanceTv = (TextView) root.findViewById(R.id.tv_balance);
        chargeBtn = (TextView) root.findViewById(R.id.chargeBtn);
        adsIv = (ImageView) root.findViewById(R.id.iv_ads);
    }

    @Override
    void onHolderCreate(View root) {
        findViews(root);
    }

    @Override
    public void showError(String errMsg) {
        // TODO: 16/4/21  error的呈现方式
    }

    @Override
    public void hideError() {

    }
}