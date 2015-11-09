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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iutiao.model.User;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.fragments.ProfileFragment;

/**
 * Created by yxy on 15/11/9.
 */
public class ProfileDialog extends DialogFragment {

    private TextView nickname;
    private TextView uid;
    private TextView balance;
    private Button dismissBtn;
    private Button bindPhoneBtn;
    private static UserManager userManager = UserManager.getInstance();

    public static ProfileDialog newInstance() {
        return new ProfileDialog();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nickname = (TextView) view.findViewById(R.id.nickname);
        uid = (TextView) view.findViewById(R.id.uid);
        balance = (TextView) view.findViewById(R.id.balance);
        dismissBtn = (Button) view.findViewById(R.id.btn_dismiss);
        bindPhoneBtn = (Button) view.findViewById(R.id.btn_bind_phone);

        User user = userManager.getCurrentUser();
        nickname.setText(user.getNickname());
        uid.setText(user.getUid());
        balance.setText(user.getBalance().toString());

        if (user.isPhone_verified()) {
            bindPhoneBtn.setEnabled(false);
            bindPhoneBtn.setText(getActivity().getString(R.string.com_iutiao_phone_bound));
        } else {
            bindPhoneBtn.setEnabled(true);
            bindPhoneBtn.setText(getActivity().getString(R.string.com_iutiao_phone_to_bind));
            bindPhoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment fragment = (ProfileFragment) getTargetFragment();
                    fragment.showDialog(PhoneNumberDialog.newInstance("bind_phone"));
                }
            });
        }

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog.this.getDialog().cancel();
            }
        });
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.com_iutiao_dialog_profile, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        onViewCreated(view, savedInstanceState);
        return dialog;
    }
}
