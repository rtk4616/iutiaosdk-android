/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iutiao.model.OKEntity;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.tasks.ResetPasswordTask;

import java.util.Map;

/**
 * Created by yxy on 15/11/6.
 */
public class ResetPasswordDialog extends VerifyCodeDialog {
    private EditText password;

    public static ResetPasswordDialog newInstance(String receiver) {
        Bundle args = new Bundle();
        args.putString("action", "reset_password");
        args.putString("receiver", receiver);
        ResetPasswordDialog fragment = new ResetPasswordDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        password = (EditText) view.findViewById(R.id.et_password);

        actionBtn.setEnabled(false);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getInputPassword().length() >= 6) {
                    if (!actionBtn.isEnabled()) {
                        actionBtn.setEnabled(true);
                    }
                } else {
                    if (actionBtn.isEnabled()) {
                        actionBtn.setEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.com_iutiao_dialog_reset_password;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_action) {
            resetPassword();
        } else if (v.getId() == R.id.btn_resend) {
            resendCode();
        }
    }

    private void resetPassword() {
        ResetPasswordTask task = new ResetPasswordTask(getActivity(), new IUTiaoCallback<OKEntity>() {
            @Override
            public void onSuccess(OKEntity t) {
                Toast.makeText(getActivity(), "password reset succeed! use your new password to login", Toast.LENGTH_LONG).show();
                ResetPasswordDialog.this.dismiss();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });
        task.execute(getParams());
    }


    @Override
    protected Map<String, Object> getParams() {
        Map<String, Object> params = super.getParams();
        params.put("password", getInputPassword());
        return params;
    }

    private String getInputPassword() {
        return password.getText().toString().trim();
    }
}
