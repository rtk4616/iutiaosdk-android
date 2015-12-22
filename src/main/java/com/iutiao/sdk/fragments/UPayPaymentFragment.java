/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.model.Charge;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoSdk;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.payment.UPayPayment;
import com.iutiao.sdk.tasks.ChargeTask;
import com.iutiao.sdk.tasks.UserProfileTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yxy on 15/12/21.
 */
public class UPayPaymentFragment extends BaseFragment implements PaymentCallback, View.OnClickListener {

    private final static String TAG = UPayPayment.class.getSimpleName();

    private List<RadioButton> upayItemViews;
    private IPayment payment;

    private GridView UpayItemsGV;
    private Button paymentBtn;
    private TextView balanceTextView;
    private TextView paymentDescTextView;
    final private String payMethod = "upay";

    private String orderid;
    private String payItem;
    private String amount = "0"; // upay 只有计费代码

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPayItem() {
        return payItem;
    }

    public void setPayItem(String payItem) {
        this.payItem = payItem;
    }

    private Map<String, Object> paymentArguments = new HashMap<>();
    public Map<String, Object> getPaymentArguments() {
        return paymentArguments;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void initPaymentArguments() {
        this.paymentArguments.put("activity", getActivity());
        this.paymentArguments.put("currency", IUTiaoSdk.getCurrency());
        this.paymentArguments.put("pay_method", getPayMethod());
        this.paymentArguments.put("orderid", newOrderid());
        this.paymentArguments.put("amount", getAmount());
        this.paymentArguments.put("pay_item", getPayItem());
    }

    /*
     * new a UUID string and set it as new orderid
     *
     */
    public String newOrderid() {
        setOrderid(UUID.randomUUID().toString());
        return getOrderid();
    }

    @Override
    public void onClick(View v) {
        RadioButton rb = (RadioButton) v;
        Toast.makeText(getActivity(), "clicked " + rb.getText().toString(), Toast.LENGTH_SHORT).show();
        for (RadioButton btn: upayItemViews) {
            if (!btn.equals(rb)) {
                btn.setChecked(false);
            } else {
                setPayItem(btn.getText().toString());
            }
        }
    }

    private void updateUI() {
        // update balance
        balanceTextView.setText(getString(R.string.com_iutiao_balance_prefix,
                UserManager.getInstance().loadProfile().getBalance()));
    }

    @Override
    public void onPaymentSuccess(PaymentResponseWrapper result) {
        Log.i(TAG, "payment success");
        Toast.makeText(getActivity(), "payment succeed", Toast.LENGTH_SHORT).show();
        UserProfileTask task = new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {

            @Override
            public void onSuccess(User user) {
                UserManager.getInstance().setCurrentUser(user);
                updateUI();
            }
            @Override
            public void onError(Exception e) {}
            @Override
            public void onCancel() {}
        });
        task.execute();
    }

    @Override
    public void onPaymentError(PaymentResponseWrapper result) {
        Toast.makeText(getActivity(), "payment failed due to " + result.error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentCancel(PaymentResponseWrapper result) {
        Toast.makeText(getActivity(), "payment canceled", Toast.LENGTH_SHORT).show();
    }

    public static UPayPaymentFragment newInstance() {
        return new UPayPaymentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_payment_upay, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        upayItemViews = new LinkedList<>();

        LinkedList<String> items = new LinkedList<>();
        items.add("2U");
        items.add("5U");
        items.add("13U");
        items.add("17U");

        balanceTextView = (TextView) view.findViewById(R.id.tv_balance);
        paymentDescTextView = (TextView) view.findViewById(R.id.tv_payment_desc);
//        paymentDescTextView.setMovementMethod(LinkMovementMethod.getInstance());

        UpayItemsGV = (GridView) view.findViewById(R.id.gv_upay_items);
        UpayItemsGV.setAdapter(new UPayItemAdapter());

        paymentBtn = (Button) view.findViewById(R.id.btn_payment);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPaymentArguments();
                createChargeOrder();
            }
        });

        for (int i=0; i<items.size(); i++) {
            RadioButton rb = newUpayItemView(items.get(i).toString(), this);
            upayItemViews.add(rb);
        }

        payment = new UPayPayment((PaymentCallback) UPayPaymentFragment.this);
        updateUI();
    }

    private RadioButton newUpayItemView(String payItem, View.OnClickListener listener) {
        RadioButton rb = new RadioButton(getActivity());
        rb.setText(payItem);
        rb.setButtonDrawable(null);
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.rbtn_selector);
        rb.setBackground(d);
        rb.setGravity(Gravity.CENTER);
        rb.setPadding(10, 10, 10, 10);
        rb.setOnClickListener(listener);
        return rb;
    }

    public String getAmount() {
        return amount;
    }

    private class UPayItemAdapter extends BaseAdapter {

        public UPayItemAdapter() {
        }

        @Override
        public int getCount() {
            return upayItemViews.size();
        }

        @Override
        public Object getItem(int position) {
            return upayItemViews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return upayItemViews.get(position);
        }
    }

    public void createChargeOrder() {
        ChargeTask task = new ChargeTask(getActivity(), new IUTiaoCallback<Charge>() {
            @Override
            public void onSuccess(Charge t) {
                payment.setPaymentArguments(getPaymentArguments());
                payment.pay();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {

            }
        });

        task.execute(paymentArguments);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called.");
        if (payment != null) {
            Log.d(TAG, "payment finished, try to clean.");
            payment.onFinish();
        }
        payment = null;
        super.onDestroy();
    }
}
