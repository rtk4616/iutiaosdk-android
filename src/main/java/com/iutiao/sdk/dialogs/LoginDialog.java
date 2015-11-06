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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Utility;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.fragments.LoginFragment;
import com.iutiao.sdk.tasks.SigninTask;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/4.
 */
public class LoginDialog extends DialogFragment {
    private EditText usernameEt;
    private EditText passwordEt;
    private Button signinBtn;
    private TextView quickRegisterTextView;
    private TextView forgotPassTextView;
    private LoginFragment fragment;

    public static LoginDialog newInstance() {
        return new LoginDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.com_iutiao_dialog_login, null);
        onViewCreated(view, savedInstanceState);
        return builder.setView(view)
                .setMessage(getActivity().getString(R.string.com_iutiao_dialog_login_message))
                .setTitle(getActivity().getString(R.string.com_iutiao_dialog_login_title))
                .create();
    }

    public String getInputUsername() {
        return usernameEt.getText().toString().trim();
    }

    public String getInputPassword() {
        return passwordEt.getText().toString().trim();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateUI() {
//        Validate.notNull(signinBtn, "signinBtn");
        signinBtn.setEnabled(false);
        usernameEt.setText(Utility.getMyPhoneNumber());
        usernameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    passwordEt.requestFocus();
                }
                return true;
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEt.getText().toString().length() >= 6) {
                    signinBtn.setEnabled(true);
                } else if (signinBtn.isEnabled()) {
                    signinBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getInputUsername();
                if (Validate.isEmailValid(username) || Validate.isPhoneValid(username)) {
                    performSignin();
                } else {
                    Log.i("REG", "username " + username + " isEmailValid " + Validate.isEmailValid(username) + " isPhoneValid " + Validate.isPhoneValid(username));
                    usernameEt.setError(getActivity().getString(R.string.com_iutiao_error_invalid_email_or_phone));
                }
            }
        });

        quickRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showQuickRegisterDialog();
            }
        });

        forgotPassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showResetPasswordDialog();
            }
        });

    }

    public void performSignin() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", getInputUsername());
        params.put("password", getInputPassword());
        SigninTask task = new SigninTask(getActivity(), (IUTiaoCallback<User>) getTargetFragment());
        task.execute(params);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragment = (LoginFragment) getTargetFragment();
        usernameEt = (EditText) view.findViewById(R.id.et_username);
        passwordEt = (EditText) view.findViewById(R.id.et_password);
        signinBtn = (Button) view.findViewById(R.id.btn_signin);
        forgotPassTextView = (TextView) view.findViewById(R.id.tv_forgot_pass);
        quickRegisterTextView = (TextView) view.findViewById(R.id.tv_quick_register);
        updateUI();
    }
}
