/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.iutiao.sdk.R;
import com.iutiao.sdk.fragments.LoginFragment;
import com.iutiao.sdk.tasks.RegisterUserTask;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/4.
 */
public class RegisterDialog extends DialogFragment {

    private EditText usernameTextView;
    private EditText password1TextView;
    private EditText password2TextView;
    private Button registerBtn;
    private static Fragment parentFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_dialog_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentFragment = (LoginFragment) getTargetFragment();

        usernameTextView = (EditText) view.findViewById(R.id.et_username);
        password1TextView = (EditText) view.findViewById(R.id.et_password1);
        password2TextView = (EditText) view.findViewById(R.id.et_password2);
        registerBtn = (Button) view.findViewById(R.id.btn_register);

        // 校验密码是否一致

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment fragment = (LoginFragment) getTargetFragment();
                RegisterUserTask task = fragment.newRegisterUserTask(getActivity());
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("username", usernameTextView.getText().toString().trim());
                params.put("password", password1TextView.getText().toString().trim());
                task.execute(params);
            }

        });

    }

    public static RegisterDialog newInstance() {
        return new RegisterDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getActivity().getString(R.string.com_iutiao_register));
        return dialog;
    }
}
