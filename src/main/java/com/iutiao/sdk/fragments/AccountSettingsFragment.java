/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;

public class AccountSettingsFragment extends Fragment {

    private LinearLayout resetPwdLL;
    private LinearLayout bindPhoneLL;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    public static AccountSettingsFragment newInstance() {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_settings, container, false);
        resetPwdLL = (LinearLayout) v.findViewById(R.id.ll_reset_pwd);
        resetPwdLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity)getActivity()).switchTo(ResetPasswordFragment.newInstance());
            }
        });
        bindPhoneLL = (LinearLayout) v.findViewById(R.id.ll_bind_phone);
        bindPhoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity)getActivity()).switchTo(PhoneVerfyFragment.newBindPhone());
            }
        });
        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
