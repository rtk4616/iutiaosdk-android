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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.BadgeView;

public class PaymentHolder extends IUTViewHolder {
    // UI related
    public GridView upayItemsGV;
    public Button paymentBtn;
    public TextView balanceTextView;
    public TextView paymentDescTextView;
    //    private Button refreshBtn;
    public BadgeView wetchatBadge;
    public BadgeView aliBadge;
    public BadgeView unionBadge;
    public BadgeView upayBadge;
    public TextView wechatPayTv;
    public TextView alipayTv;
    public TextView unionPayTv;
    public TextView upayTv;

    private PaymentHolder(Context context, View view) {
        super(context, view);
    }

    public static PaymentHolder Create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        PaymentHolder paymentHolder = new PaymentHolder(context, inflater.inflate(R.layout.com_iutiao_fragment_payment_upay, null, false));
        return paymentHolder;
    }

    private void findViews(View root) {
        balanceTextView = (TextView) root.findViewById(R.id.tv_balance);
        paymentDescTextView = (TextView) root.findViewById(R.id.tv_payment_desc);
        upayItemsGV = (GridView) root.findViewById(R.id.gv_upay_items);
        paymentBtn = (Button) root.findViewById(R.id.btn_payment);
        wechatPayTv = (TextView) root.findViewById(R.id.wechatpay);
        alipayTv = (TextView) root.findViewById(R.id.tv_alipay);
        upayTv = (TextView) root.findViewById(R.id.tv_upay);
        unionPayTv = (TextView) root.findViewById(R.id.tv_unionpay);
        wetchatBadge = badge(wechatPayTv);
        aliBadge = badge(alipayTv);
        unionBadge = badge(unionPayTv);
        upayBadge = badge(upayTv);
    }

    public void hideBadges() {
        wetchatBadge.hide();
        aliBadge.hide();
        unionBadge.hide();
        upayBadge.hide();
    }

    public BadgeView badge(View view) {
        BadgeView badgeView = new BadgeView(context, view);
        badgeView.setTextColor(Color.WHITE);
        badgeView.setBackgroundResource(R.drawable.pay_sel);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeView.setAlpha(1f);
        badgeView.setBadgeMargin(0, 0);
        return badgeView;
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