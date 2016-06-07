/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.model;

import com.iutiao.sdk.utils.IUTTextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by yxy on 15/12/3.
 */
public class OrderInfo {
    private String sku;
    private Integer quanilty;
    private String appOrderId;
    private String extra;
    private String postback;

    private OrderInfo(String sku, Integer quanilty, String appOrderId, String extra, String postback) {
        this.sku = sku;
        this.quanilty = quanilty;
        this.appOrderId = appOrderId;
        this.extra = extra;
        this.postback = postback;
    }

    private OrderInfo(Builder builder) {
        this.sku = IUTTextUtils.nullIfBlank(builder.sku);
        this.quanilty = builder.quanilty;
        this.appOrderId = IUTTextUtils.nullIfBlank(builder.appOrderId);
        this.extra = IUTTextUtils.nullIfBlank(builder.extra);
        this.postback = IUTTextUtils.nullIfBlank(builder.postback);
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuanilty() {
        return quanilty;
    }

    public void setQuanilty(Integer quanilty) {
        this.quanilty = quanilty;
    }

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPostback() {
        return postback;
    }

    public void setPostback(String postback) {
        this.postback = postback;
    }

    public static class Builder {
        private String sku;
        private Integer quanilty = 1;
        private String appOrderId;
        private String extra;
        private String postback;

        public OrderInfo build() {
            return new OrderInfo(this);
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder quanilty(Integer quanilty) {
            this.quanilty = quanilty;
            return this;
        }

        public Builder appOrderId(String appOrderId) {
            this.appOrderId = appOrderId;
            return this;
        }

        public Builder postback(String postback) {
            this.postback = postback;
            return this;
        }

        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("sku", IUTTextUtils.nullIfBlank(this.getSku()));
        params.put("quanilty", this.getQuanilty());
        params.put("app_orderid", IUTTextUtils.nullIfBlank(this.getAppOrderId()));
        params.put("extra", IUTTextUtils.nullIfBlank(this.getExtra()));
        params.put("postback", IUTTextUtils.nullIfBlank(this.getPostback()));

        // remove null value
        for (String key : new HashSet<>(params.keySet())) {
            if (params.get(key) == null) {
                params.remove(key);
            }
        }
        return params;
    }

    public boolean validate() {
        // TODO 订单校验参数
        return true;
    }
}
