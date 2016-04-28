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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.iutiao.model.Charge;
import com.iutiao.model.UPayItem;
import com.iutiao.model.UPayItemCollection;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoSdk;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.holders.PaymentHolder;
import com.iutiao.sdk.payment.IPayment;
import com.iutiao.sdk.payment.PassionPayment;
import com.iutiao.sdk.payment.PaymentCallback;
import com.iutiao.sdk.payment.PaymentResponseWrapper;
import com.iutiao.sdk.payment.UPayPayment;
import com.iutiao.sdk.tasks.ChargeTask;
import com.iutiao.sdk.tasks.UPayItemCollectionTask;
import com.iutiao.sdk.tasks.UserProfileTask;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yxy on 15/12/21.
 */
// TODO: 16/4/28 Code cleanup and refactoring
public class UPayPaymentFragment extends BaseFragment implements PaymentCallback {
    private final static String TAG = UPayPayment.class.getSimpleName();
    private final static int PROFILE_UPDATED = 201;

    private PaymentHolder paymentHolder;
    private List<UPayItem> upayItems;
    //    final private String payMethod = "upay";
    private String payMethod = "payssion";
    private String pmId = "qiwi";
    private UPayItemAdapter upayItemAdapter;
    private List<RadioButton> upayItemViews;
    private EditText upayItemEt;
    private Double currentBalance;
    private boolean isFirstTime = true;

    public String getPmId() {
        return pmId;
    }

    private static class MyHandler extends Handler {
        private WeakReference<UPayPaymentFragment> fragment;

        public MyHandler(UPayPaymentFragment fragment) {
            this.fragment = new WeakReference<UPayPaymentFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            UPayPaymentFragment fragment = this.fragment.get();
            switch (msg.what) {
                case PROFILE_UPDATED:
                    User user = (User) msg.obj;
                    Log.i(TAG, "profile updated " + user);
                    UserManager.getInstance().setCurrentUser(user);
                    fragment.setCurrentBalance(user.getBalance());
                    fragment.updateUI();
            }
        }
    }

    private final MyHandler handler = new MyHandler(this);

    private IPayment payment;

    private String orderid;
    private String payItem;

    public void setAmount(String amount) {
        this.amount = amount;
    }

    private String amount = "10.0"; // upay 只有计费代码

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
        this.paymentArguments.put("pmid", getPmId());
    }

    /*
     * new a UUID string and set it as new orderid
     */
    public String newOrderid() {
        setOrderid(UUID.randomUUID().toString());
        return getOrderid();
    }

    public void updateUI() {
        // update balance
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (isAdded()) {
            paymentHolder.balanceTextView.setText(getString(R.string.com_iutiao_balance_prefix,
                    formatter.format(getCurrentBalance())));
        }
    }

    class UpdateUserProfileTask implements Runnable {

        @Override
        public void run() {
            try {
                User user = User.profile();
                Message message = handler.obtainMessage(PROFILE_UPDATED, user);
                message.sendToTarget();
            } catch (Exception e) {
                Log.e(TAG, "fetch user profile failed", e);
            }
        }
    }

    private void updateBalance() {
        UserProfileTask task = new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {

            @Override
            public void onSuccess(User user) {
                UserManager.getInstance().setCurrentUser(user);
                setCurrentBalance(user.getBalance());
                updateUI();
            }

            @Override
            public void onError(Exception e) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onExecuted() {

            }
        });
        task.execute();
    }

    @Override
    public void onPaymentSuccess(PaymentResponseWrapper result) {
        Double ucoin = getUCoin(getPayItem());
        Double balance = getCurrentBalance() + ucoin;
        Log.i(TAG, String.format("Payment successful. Item %s purchased, cost %.2f ucoin, current balance are %.2f",
                getPayItem(), ucoin, balance));
        setCurrentBalance(balance);
        updateUI();
//        new Thread(new UpdateUserProfileTask()).start();
    }

    private Double getUCoin(String upayItem) {
        for (UPayItem item : upayItems) {
            if (item.getItemId().equals(upayItem)) {
                return item.getUcoin();
            }
        }
        return 0.00;
    }

    @Override
    public void onPaymentError(PaymentResponseWrapper result) {
        Log.i(TAG, "payment failed due to " + result.error.toString() + " data " + result.data.toString());
        Toast.makeText(getActivity(), "payment failed due to " + result.error.toString() + " data " + result.data.toString(), Toast.LENGTH_SHORT).show();
        updateBalance();
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
        paymentHolder = PaymentHolder.Create(getActivity());
        return paymentHolder.root;
    }

    /*
     * 初始化 UI 相关的代码
     */
    private void initUI() {
        upayItemAdapter = new UPayItemAdapter(getActivity());
        paymentHolder.upayItemsGV.setAdapter(upayItemAdapter);
        paymentHolder.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPaymentArguments();
                createChargeOrder();
            }
        });
// TODO: 16/4/22 payment component
        paymentHolder.qiwiBadge.show();
//        badgeView.hide();
        paymentHolder.qiwiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentHolder.hideBadges();
                paymentHolder.qiwiBadge.toggle();
                payMethod = "payssion";
                pmId = "qiwi";
                payment = new PassionPayment((PaymentCallback) UPayPaymentFragment.this);
                isFirstTime = true;
                initPayItems();
                setAmount(String.valueOf(upayItems.get(0).getUcoin() * 10));
            }
        });
        paymentHolder.yamoneyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentHolder.hideBadges();
                paymentHolder.yamoneyBadge.toggle();
                payMethod = "payssion";
                pmId = "yamoney";
                payment = new PassionPayment((PaymentCallback) UPayPaymentFragment.this);
                isFirstTime = true;
                initPayItems();
                setAmount(String.valueOf(upayItems.get(0).getUcoin() * 10));
            }
        });
        paymentHolder.upayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentHolder.hideBadges();
                paymentHolder.upayBadge.toggle();
                payMethod = "upay";
                setAmount("0");
                payment = new UPayPayment((PaymentCallback) UPayPaymentFragment.this);
                isFirstTime = true;
                initPayItems();
            }
        });
        setCurrentBalance(UserManager.getInstance().getCurrentUser().getBalance());
        payment = new PassionPayment((PaymentCallback) UPayPaymentFragment.this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 让upay 实例在入口程序中重新初始化
        IUTiaoSdk.setUpayInitialized(false);

        upayItemViews = new LinkedList<RadioButton>();
        initPayItems(); // 初始化 upay 计费代码
        initUI();
        updateUI();

        new Thread(new UpdateUserProfileTask()).start();
    }

    public String getAmount() {
        return amount;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    // TODO: 16/4/22 adapter 分离
    private class UPayItemAdapter extends BaseAdapter {

        private Context context;

        public UPayItemAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (!payMethod.equals("upay")) {
                return upayItems.size() + 1;
            } else {
                return upayItems.size();
            }
        }

        @Override
        public String getItem(int position) {
            return upayItems.get(position).getItemId();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (position == upayItems.size()) {
                EditText view;
                view = (EditText) LayoutInflater.from(this.context).inflate(R.layout.com_iutiao_upay_input_item, parent, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        for (RadioButton btn : upayItemViews) {
                            btn.setChecked(false);
                        }
                        return false;
                    }
                });
                view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        setAmount(String.valueOf(Double.valueOf(String.valueOf(s)) * 10));
//                        setPayItem(null);
                    }
                });
                upayItemEt = view;
                return view;
            } else {
                RadioButton v;
                v = (RadioButton) LayoutInflater.from(this.context).inflate(R.layout.com_iutiao_upay_item, parent, false);
                String item = getItem(position);
                v.setText(item);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton rb = (RadioButton) v;
                        if (upayItemEt != null) {
                            upayItemEt.clearFocus();
                        }
                        if (payMethod.equals("upay")) {
                            setPayItem(rb.getText().toString());
                            setAmount("0");
                        } else {
                            setAmount(String.valueOf(upayItems.get(position).getUcoin() * 10));
                        }

                        for (RadioButton btn : upayItemViews) {
                            if (!btn.equals(rb)) {
                                btn.setChecked(false);
                            }
                        }
                    }
                });
                if (position == 0) {
                    v.setChecked(true);
                    if(payMethod.equals("upay")){
                        setPayItem(item);
                    }
                }
                upayItemViews.add(v);
                return v;
            }

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

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onExecuted() {

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
    private void initPayItems() {
        final List payItems = new LinkedList<>();
        upayItems = new LinkedList<>();
        if (payMethod.equals("upay")) {
            UPayItemCollectionTask task = new UPayItemCollectionTask(getActivity(), new IUTiaoCallback<UPayItemCollection>() {
                @Override
                public void onSuccess(UPayItemCollection collection) {
                    for (UPayItem item : collection.getResults()) {
                        payItems.add(item);
                    }
                    if (upayItemAdapter != null) {
                        upayItems = payItems;
                        upayItemAdapter.notifyDataSetChanged();
                    }
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

                @Override
                public void onPreExecute() {

                }

                @Override
                public void onExecuted() {

                }
            });
            task.execute();
        } else {
            upayItems = defaultPayItems();
            if (upayItemAdapter != null) {
                upayItemAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<UPayItem> defaultPayItems() {
        List<UPayItem> items = new LinkedList<>();
        List<Double> ucoins = new LinkedList<>();
        ucoins.add(1.00);
        ucoins.add(5.00);
        ucoins.add(10.00);
        ucoins.add(25.00);
        ucoins.add(50.00);

        NumberFormat formatter = new DecimalFormat("#0");
        for (Double u : ucoins) {
            UPayItem item = new UPayItem();
            item.setDesc("");
            item.setUcoin(u);
            item.setItemId(formatter.format(u) + "U");
            items.add(item);
        }
        return items;
    }

    /*
     * 默认的 upay 计费代码
     */
    private List<UPayItem> defaultUPayItems() {
        List<UPayItem> items = new LinkedList<>();
        List<Double> ucoins = new LinkedList<>();
        ucoins.add(2.00);
        ucoins.add(5.00);
        ucoins.add(13.00);
        ucoins.add(17.00);

        NumberFormat formatter = new DecimalFormat("#0");
        for (Double u : ucoins) {
            UPayItem item = new UPayItem();
            item.setDesc("");
            item.setUcoin(u);
            item.setItemId(formatter.format(u) + "U");
            items.add(item);
        }
        return items;
    }

}
