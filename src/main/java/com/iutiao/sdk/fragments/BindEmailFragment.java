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
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.views.IUTTitleBar;

public class BindEmailFragment extends Fragment {

    private LinearLayout resetPwdLL;
    private LinearLayout bindPhoneLL;
    private TextView phoneBindStateTv;
    private TextView emailBindStateTv;
    private IUTTitleBar title;

    public BindEmailFragment() {
        // Required empty public constructor
    }

    public static BindEmailFragment newInstance() {
        BindEmailFragment fragment = new BindEmailFragment();
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
        resetPwdLL = (LinearLayout) v.findViewById(R.id.ll_reset_pwd);
        bindPhoneLL = (LinearLayout) v.findViewById(R.id.ll_bind_phone);
        title = (IUTTitleBar) v.findViewById(R.id.iuttb_title);
        User user = UserManager.getInstance().getCurrentUser();
        title.setTitle(user.getNickname());
        if(user.isPhone_verified()){
            phoneBindStateTv.setText("已关联");
        }else{
            bindPhoneLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IUTiaoDevActivity)getActivity()).switchTo(PhoneVerfyFragment.newBindPhone());
                }
            });
        }
        if(user.isEmail_verified()){
            emailBindStateTv.setText("已关联");
        }else{
            // TODO: 16/4/25 绑定登陆邮箱
        }
        resetPwdLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserManager.getInstance().getCurrentUser().isEmail_verified()||!UserManager.getInstance().getCurrentUser().isPhone_verified()){
                    Toast.makeText(getActivity(), "请先绑定手机或邮箱", Toast.LENGTH_SHORT).show();
                }else{
                    ((IUTiaoDevActivity)getActivity()).switchTo(ChangePasswordFragment.newInstance());
                }
            }
        });
        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
