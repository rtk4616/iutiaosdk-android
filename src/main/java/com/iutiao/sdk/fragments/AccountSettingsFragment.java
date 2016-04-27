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
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.tasks.UserProfileTask;
import com.iutiao.sdk.views.IUTTitleBar;

public class AccountSettingsFragment extends Fragment {

    private LinearLayout resetPwdLL;
    private LinearLayout bindPhoneLL;
    private TextView phoneBindStateTv;
    private TextView emailBindStateTv;
    private TextView uidTv;
    private IUTTitleBar title;
    private LinearLayout bindEmailLL;

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
        View v = inflater.inflate(R.layout.com_iutiao_fragment_account_settings, container, false);
        phoneBindStateTv = (TextView) v.findViewById(R.id.tv_phone_bind_state);
        emailBindStateTv = (TextView) v.findViewById(R.id.tv_email_bind_state);
        uidTv= (TextView) v.findViewById(R.id.tv_uid);
        resetPwdLL = (LinearLayout) v.findViewById(R.id.ll_reset_pwd);
        bindPhoneLL = (LinearLayout) v.findViewById(R.id.ll_bind_phone);
        bindEmailLL = (LinearLayout) v.findViewById(R.id.ll_bind_email);
        title = (IUTTitleBar) v.findViewById(R.id.iuttb_title);
        User currentUser = UserManager.getInstance().getCurrentUser();
        title.setTitle(currentUser.getNickname());
        uidTv.setText(currentUser.getUid());
        if(currentUser.isPhone_verified()){
            phoneBindStateTv.setText(R.string.com_iutiao_tips_bound);
        }
        if(currentUser.isEmail_verified()){
            emailBindStateTv.setText(R.string.com_iutiao_tips_bound);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                title.setTitle(user.getNickname());
                if(user.isPhone_verified()){
                    phoneBindStateTv.setText(R.string.com_iutiao_tips_bound);
                }else{
                    bindPhoneLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((IUTiaoDevActivity)getActivity()).switchTo(PhoneVerfyFragment.newBindPhone());
                        }
                    });
                }
                if(user.isEmail_verified()){
                    emailBindStateTv.setText(R.string.com_iutiao_tips_bound);
                }else{
                    bindEmailLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((IUTiaoDevActivity) getActivity()).switchTo(BindEmailFragment.newInstance());
                        }
                    });
                }
                resetPwdLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().getCurrentUser().isEmail_verified() || UserManager.getInstance().getCurrentUser().isPhone_verified()) {
                            ((IUTiaoDevActivity) getActivity()).switchTo(ChangePasswordFragment.newInstance());
                        } else {
                            Toast.makeText(getActivity(), R.string.com_iutiao_tips_ask_bind, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        }).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
