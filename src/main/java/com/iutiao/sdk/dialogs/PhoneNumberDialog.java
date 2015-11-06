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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Utility;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.fragments.LoginFragment;
import com.iutiao.sdk.tasks.SMSTask;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/4.
 */
public class PhoneNumberDialog extends DialogFragment {

    private TextView phonenumber;
    private Button nextBtn;
    public static final String TAG = "PhoneNumberDialog";
    public static LoginFragment fragment;
    private String action;

    public enum ACTIONS {
        register,
        reset_password,
        ;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.com_iutiao_dialog_phone, null);
        Dialog d = builder
                .setView(view)
                .setTitle(getActivity().getString(R.string.com_iutiao_dialog_phone_title))
                .setMessage(getActivity().getString(R.string.com_iutiao_dialog_phone_message))
                .create();
        onViewCreated(view, savedInstanceState);
        action = getArguments().getString("action");
        Validate.isInEnum(action, ACTIONS.class);
        return d;
    }

    public String getPhoneNumber() {
        return phonenumber.getText().toString().trim();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = (LoginFragment) getTargetFragment();

        phonenumber = (TextView) view.findViewById(R.id.et_phone);
        final String myPhoneNumber = Utility.getMyPhoneNumber();
        if (myPhoneNumber != null) {
            phonenumber.setText(myPhoneNumber);
        }
        nextBtn = (Button) view.findViewById(R.id.btn_next);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSTask task = new SMSTask(getActivity(), new IUTiaoCallback() {
                    @Override
                    public void onSuccess(Object t) {
                        Log.i(TAG, "sms action " + action + " requested");
                        if (action.equals(ACTIONS.register.name())) {
                            fragment.showDialog(VerifyCodeDialog.newInstance(getPhoneNumber(), action));
                        } else if (action.equals(ACTIONS.reset_password.name())) {
                            fragment.showDialog(ResetPasswordDialog.newInstance(getPhoneNumber()));
                        }
//                        PhoneNumberDialog.this.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "something went wrong when sms created", e);
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("receiver", getPhoneNumber());
                params.put("action", action);
                task.execute(params);
            }
        });
    }

    public static PhoneNumberDialog newInstance(String action) {
        Bundle args = new Bundle();
        args.putString("action", action);
        PhoneNumberDialog dialog = new PhoneNumberDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static PhoneNumberDialog newInstance() {
        return newInstance(ACTIONS.register.name());
    }


}
