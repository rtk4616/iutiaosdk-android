/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.model.Charge;
import com.iutiao.model.UPayItem;
import com.iutiao.model.UPayItemCollection;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoSdk;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.payment.UPayPayment;
import com.iutiao.sdk.tasks.ChargeTask;
import com.iutiao.sdk.tasks.UPayItemCollectionTask;
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

    private List<String> upayItems;
    private GridView upayItemsGV;
    private Button paymentBtn;
    private TextView balanceTextView;
    private TextView paymentDescTextView;
    final private String payMethod = "upay";
    private UPayItemAdapter upayItemAdapter;
    private List<RadioButton> upayItemViews;

    private IPayment payment;

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
     */
    public String newOrderid() {
        setOrderid(UUID.randomUUID().toString());
        return getOrderid();
    }

    @Override
    public void onClick(View v) {
        RadioButton rb = (RadioButton) v;
        for (RadioButton btn: upayItemViews) {
            if (!btn.equals(rb)) {
                btn.setChecked(false);
            } else {
                String item = btn.getText().toString();
                setPayItem(item);
                Log.d(TAG, "clicked " + item);
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

        upayItemViews = new LinkedList<RadioButton>();
        initUPayItems();

        balanceTextView = (TextView) view.findViewById(R.id.tv_balance);
        paymentDescTextView = (TextView) view.findViewById(R.id.tv_payment_desc);

        upayItemsGV = (GridView) view.findViewById(R.id.gv_upay_items);
        upayItemAdapter = new UPayItemAdapter(getActivity());
        upayItemsGV.setAdapter(upayItemAdapter);
        paymentBtn = (Button) view.findViewById(R.id.btn_payment);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPaymentArguments();
                createChargeOrder();
            }
        });

        payment = new UPayPayment((PaymentCallback) UPayPaymentFragment.this);
        updateUI();
    }

    public String getAmount() {
        return amount;
    }

    private class UPayItemAdapter extends BaseAdapter {

        private Context context;

        public UPayItemAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return upayItems.size();
        }

        @Override
        public Object getItem(int position) {
            return upayItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "position " + position + " convertView " + convertView);
            RadioButton v;
            if (convertView == null) {
                Log.i(TAG, "inflate upay item view");
                v = (RadioButton) LayoutInflater.from(this.context).inflate(R.layout.com_iutiao_upay_item, parent, false);
                String item = upayItems.get(position);
                v.setText(item);
                v.setOnClickListener((View.OnClickListener) UPayPaymentFragment.this);
                if (position == 0) {
                    v.setChecked(true);
                    setPayItem(item);
                }
                upayItemViews.add(v);
            } else {
                v = (RadioButton) convertView;
            }
            return v;
        }
    }

    /*
     * 创建充值订单
     */
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
        if (payment != null) {
            Log.d(TAG, "payment finished, try to clean.");
            payment.onFinish();
        }
        payment = null;
        super.onDestroy();
    }

    /*
     * 初始化 upay 计费代码条目
     */
    private void initUPayItems() {
        upayItems = new LinkedList<>();
        UPayItemCollectionTask task = new UPayItemCollectionTask(getActivity(), new IUTiaoCallback<UPayItemCollection>() {
            @Override
            public void onSuccess(UPayItemCollection collection) {
                for (UPayItem item : collection.getResults()) {
                   upayItems.add(item.getItemId());
                }
                Log.i(TAG, "Got upay items " + upayItems.toString());
                upayItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // 读取默认的upay 计费代码
                Log.e(TAG, "fetch upay items faield", e);
                upayItems = defaultUPayItems();
            }

            @Override
            public void onCancel() {
            }
        });
        task.execute();
    }

    private List<String> defaultUPayItems() {
        List<String> items = new LinkedList<>();
        items.add("2U");
        items.add("5U");
        items.add("13U");
        return items;
    }
}
