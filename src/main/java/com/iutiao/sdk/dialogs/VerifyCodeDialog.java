/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iutiao.model.OKEntity;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.tasks.BindPhoneTask;
import com.iutiao.sdk.tasks.RegisterPhoneTask;
import com.iutiao.sdk.tasks.SMSTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yxy on 15/11/6.
 */
public class VerifyCodeDialog extends DialogFragment implements View.OnClickListener {

    String receiver;
    String action;
    Button actionBtn;
    Button resendCodeBtn;
    EditText verifyCodeEt;
    private CountDownTask countDownTask;
    private static final String TAG = VerifyCodeDialog.class.getSimpleName();

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiver = getArguments().getString("receiver");
        action = getArguments().getString("action");

        // view lookups
        actionBtn = (Button) view.findViewById(R.id.btn_action);
        resendCodeBtn = (Button) view.findViewById(R.id.btn_resend);
        verifyCodeEt = (EditText) view.findViewById(R.id.et_code);

        resendCodeBtn.setOnClickListener(this);
        actionBtn.setOnClickListener(this);
        startCountDown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i(TAG, "cancel countdown task");
        if (countDownTask != null) {
            if (!countDownTask.isCancelled()) {
                countDownTask.cancel(true);
            }
        }
    }

    private class CountDownTask extends AsyncTask<Void, Integer, Void> {
        private int i=60;
        private Button btn;

        public CountDownTask(Button btn) {
            this.btn = btn;
        }

        @Override
        protected void onPreExecute() {
            this.btn.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.btn.setEnabled(true);
            this.btn.setText(getActivity().getString(R.string.com_iutiao_verify_resend));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setCountdown(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (i > 0) {
                if (isCancelled()) {
                    return null;
                }
                try {
                    Thread.sleep(1000 * 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
                publishProgress(i);
            }
            return null;
        }

        private void setCountdown(Integer value) {
            btn.setText(String.format("%d s", value));
        }
    }



    public static VerifyCodeDialog newInstance(String receiver, String action) {
        Bundle args = new Bundle();
        args.putString("receiver", receiver);
        args.putString("action", action);
        VerifyCodeDialog fragment = new VerifyCodeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public int getLayoutResource() {
        return R.layout.com_iutiao_dialog_phone_verify;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(getLayoutResource(), null);
        Dialog d = builder
                .setMessage(getActivity().getString(R.string.com_iutiao_verify_label))
                .setTitle(getActivity().getString(R.string.com_iutiao_verify_title))
                .setView(view)
                .create();

        // 确保 onViewCreated 被调用
        onViewCreated(view, savedInstanceState);
        return d;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_action) {
            if (action.equals("register")) {
                quickRegister();
            } else if (action.equals("bind_phone")) {
                bindPhone();
            }
        } else if (v.getId() == R.id.btn_resend) {
            resendCode();
        }
    }

    protected Map<String, Object> getParams() {
        HashMap<String, Object> p = new HashMap<>();
        p.put("phone_number", receiver);
        p.put("code", getVerifyCode());
        return p;
    }

    private void bindPhone() {
        BindPhoneTask task = new BindPhoneTask(getActivity(), new IUTiaoCallback<OKEntity>() {
            @Override
            public void onSuccess(OKEntity t) {
                Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
                VerifyCodeDialog.this.dismiss();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), "error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
        Map<String, Object> p = getParams();
        p.put("id", UserManager.getInstance().getCurrentUser().getUid());
        task.execute(p);
    }

    public String getVerifyCode() {
        return verifyCodeEt.getText().toString().trim();
    }

    private void quickRegister() {
        RegisterPhoneTask task = new RegisterPhoneTask(getActivity(), (IUTiaoCallback<User>) getTargetFragment());
        task.execute(getParams());
    }

    public void resendCode() {

        SMSTask task = new SMSTask(getActivity(), new IUTiaoCallback() {
            @Override
            public void onSuccess(Object t) {
                Log.i(TAG, "re-request sms confirmation code");
                Toast.makeText(getActivity(), "request has been sent, please wait for the SMS", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "re-request sms confirmation code error", e);
                Toast.makeText(getActivity(), "error:" + e.toString(), Toast.LENGTH_SHORT).show();
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
        HashMap<String, Object> p = new HashMap<String, Object> (){{
            put("receiver", receiver);
            put("action", action);
        }};
        task.execute(p);
        startCountDown();
    }

    public void startCountDown() {
        // execute parallel
        countDownTask = new CountDownTask(resendCodeBtn);
        countDownTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
