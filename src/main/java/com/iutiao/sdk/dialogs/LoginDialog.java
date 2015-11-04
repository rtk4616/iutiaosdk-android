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
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.fragments.LoginFragment;

/**
 * Created by yxy on 15/11/4.
 */
public class LoginDialog extends DialogFragment {
    private Button signinBtn;
    private TextView quickRegisterTextView;
    private TextView forgotPassTextView;

    public static LoginDialog newInstance() {
        return new LoginDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_dialog_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LoginFragment fragment = (LoginFragment) getTargetFragment();

        signinBtn = (Button) view.findViewById(R.id.btn_signin);

        quickRegisterTextView = (TextView) view.findViewById(R.id.tv_quick_register);
        quickRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showQuickRegisterDialog();
            }
        });

        forgotPassTextView = (TextView) view.findViewById(R.id.tv_forgot_pass);

    }
}
