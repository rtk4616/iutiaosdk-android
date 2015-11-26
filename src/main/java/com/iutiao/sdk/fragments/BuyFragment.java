/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.iutiao.model.AppOrder;
import com.iutiao.sdk.IUTiaoActivity;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.tasks.AppOrderTask;
import com.iutiao.sdk.tasks.IUTiaoRequestTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yxy on 15/11/13.
 *
 * TODO: 创建一个订单支付的 task,
 * 逻辑如下
 * 1. 提供订单参数（cp 订单号， 产品 ID， 购买数量）
 * 2. 向服务器下单， 如果用户余额足够， 扣款支付， 并通知 cp 端发货
 * 3. 如果用户余额不足， 客户端 listener 启动第三方支付活动界面。
 * 4. 用户扣费成功， 我方服务端收到通知，并从通知中取出需要支付的订单号
 *    余额充足， 扣款， 余额不充足， 返回游戏界面
 *
 */
public class BuyFragment extends BaseFragment implements View.OnClickListener, IUTiaoCallback<AppOrder> {

    private static Map<Integer, String> items ;

    public static void initProductItems() {
        items = new HashMap<Integer, String>();
        items.put(R.id.btn_gold_5, "com.sku.gold.5");
        items.put(R.id.btn_gold_10, "com.sku.gold.10");
        items.put(R.id.btn_gold_15, "com.sku.gold.15");
        items.put(R.id.btn_unlock, "com.sku.unlock.all");
        items.put(R.id.btn_armour, "com.sku.armour.1");
    }

    public static BuyFragment newInstance() {
        initProductItems();
        return new BuyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_buy, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_gold_5).setOnClickListener(this);
        view.findViewById(R.id.btn_gold_10).setOnClickListener(this);
        view.findViewById(R.id.btn_gold_15).setOnClickListener(this);

        view.findViewById(R.id.btn_unlock).setOnClickListener(this);
        view.findViewById(R.id.btn_armour).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        HashMap<String, Object> params = new HashMap<>();
        params.put("sku", items.get(id));
        params.put("quanilty", 1);
        params.put("q", "非法参数");
        params.put("app_orderid", getAppOrderid());
        params.put("extra", AppOrder.GSON.toJson(params));
        params.put("postback", "http://192.168.1.106:8000/v1/service/postback/");
        AppOrderTask task = new AppOrderTask(getActivity(), this, "create");
        task.execute(params);
    }

    public String getAppOrderid() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void onSuccess(AppOrder order) {
        Toast.makeText(getActivity(), order.toString(), Toast.LENGTH_LONG).show();
        if (!order.getStatus().equals("paid")) {
            Toast.makeText(getActivity(), "not enough money", Toast.LENGTH_SHORT).show();
            Intent i = IUTiaoActivity.newIntent(getActivity(), "charge");
            startActivityForResult(i, 12);
        }
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {

    }
}
