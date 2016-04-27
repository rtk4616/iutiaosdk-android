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
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.holders.AccountSettingsHolder;
import com.iutiao.sdk.tasks.UserProfileTask;

public class AccountSettingsFragment extends Fragment {

private AccountSettingsHolder accountSettingsHolder;

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
        accountSettingsHolder = AccountSettingsHolder.Create(getActivity());
        updateProfile();
        return accountSettingsHolder.root;
    }

    private void updateProfile() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        accountSettingsHolder.title.setTitle(currentUser.getNickname());
        accountSettingsHolder.uidTv.setText(currentUser.getUid());
        if(currentUser.isPhone_verified()){
            accountSettingsHolder.phoneBindStateTv.setText(R.string.com_iutiao_tips_bound);
            accountSettingsHolder.phoneBindStateTv.setOnClickListener(null);
            accountSettingsHolder.phoneBindStateTv.setCompoundDrawables(null, null, null, null);
            accountSettingsHolder.phoneBindStateTv.setCompoundDrawables(null, null, null, null);
        }else{
            accountSettingsHolder.bindPhoneLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IUTiaoDevActivity)getActivity()).switchTo(PhoneVerfyFragment.newBindPhone());
                }
            });
        }
        if(currentUser.isEmail_verified()){
            accountSettingsHolder.emailBindStateTv.setText(R.string.com_iutiao_tips_bound);
            accountSettingsHolder.emailBindStateTv.setOnClickListener(null);
            accountSettingsHolder.emailBindStateTv.setCompoundDrawables(null, null, null, null);
        }else{
            accountSettingsHolder.bindEmailLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IUTiaoDevActivity) getActivity()).switchTo(BindEmailFragment.newInstance());
                }
            });
        }
        accountSettingsHolder.resetPwdLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().getCurrentUser().isEmail_verified() || UserManager.getInstance().getCurrentUser().isPhone_verified()) {
                    ((IUTiaoDevActivity) getActivity()).switchTo(ChangePasswordFragment.newInstance());
                } else {
                    Toast.makeText(getActivity(), R.string.com_iutiao_tips_ask_bind, Toast.LENGTH_SHORT).show();
                }
            }
        });
        accountSettingsHolder.nicknameLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(EditNickNameFragment.newInstance());
            }
        });
        accountSettingsHolder.balanceTv.setText(currentUser.getBalance() + getString(R.string.com_iutiao_balance_suffixes));
        accountSettingsHolder.nicknameTv.setText(currentUser.getNickname());
//        accountSettingsHolder.nicknameTv.setCompoundDrawables(null, null, null, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                updateProfile();
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
