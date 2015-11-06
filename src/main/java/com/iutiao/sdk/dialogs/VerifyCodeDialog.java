/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.tasks.RegisterPhoneTask;
import com.iutiao.sdk.tasks.RegisterUserTask;
import com.iutiao.sdk.tasks.SMSTask;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/6.
 */
public class VerifyCodeDialog extends DialogFragment implements View.OnClickListener {

    private String receiver;
    private String action;
    private String verifyCode;
    private Button verifyBtn;
    private Button resendCodeBtn;
    private EditText verifyCodeEt;
    private static final String TAG = VerifyCodeDialog.class.getSimpleName();

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiver = getArguments().getString("receiver");
        action = getArguments().getString("action");

        // view lookups
        verifyBtn = (Button) view.findViewById(R.id.btn_verify);
        resendCodeBtn = (Button) view.findViewById(R.id.btn_resend);
        verifyCodeEt = (EditText) view.findViewById(R.id.et_code);


        resendCodeBtn.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);
    }

    public static VerifyCodeDialog newInstance(String receiver, String action) {
        Bundle args = new Bundle();
        args.putString("receiver", receiver);
        args.putString("action", action);
        VerifyCodeDialog fragment = new VerifyCodeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.com_iutiao_dialog_phone_verify, null);
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
        if (v.getId() == R.id.btn_verify) {
            quickRegister();
        } else if (v.getId() == R.id.btn_resend) {
            resendCode();
        }
    }

    private void quickRegister() {
        RegisterPhoneTask task = new RegisterPhoneTask(getActivity(), (IUTiaoCallback<User>) getTargetFragment());
        verifyCode = verifyCodeEt.getText().toString().trim();
        HashMap<String, Object> p = new HashMap<>();
        p.put("phone_number", receiver);
        p.put("code", verifyCode);
        Log.i(TAG, "params" + p.toString());
        task.execute(p);
    }

    private void resendCode() {
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
        });
        HashMap<String, Object> p = new HashMap<String, Object> (){{
            put("receiver", receiver);
            put("action", action);
        }};
        task.execute(p);
    }
}
