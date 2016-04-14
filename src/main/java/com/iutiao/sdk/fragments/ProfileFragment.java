/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.UserProfileTask;
import com.iutiao.sdk.views.IUTTitleBar;

/**
 * Created by yxy on 15/11/7.
 */

public class ProfileFragment extends BaseFragment {
    // TODO: 16/4/14 progress,holder
    private IUTTitleBar titleBar;
    private TextView chargeBtn;
    private TextView balanceTv;

    public static final UserManager userManager = UserManager.getInstance();
    private static final String TAG = ProfileFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.com_iutiao_fragment_profile, container, false);
        titleBar = (IUTTitleBar) v.findViewById(R.id.title);
        balanceTv = (TextView) v.findViewById(R.id.tv_balance);
        chargeBtn = (TextView) v.findViewById(R.id.chargeBtn);
        titleBar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.settings, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.account_settings) {
                            ((IUTiaoDevActivity) getActivity()).switchTo(AccountSettingsFragment.newInstance());
                        } else if (i == R.id.logout) {
                            LoginManager.getInstance().logOut();
                            getActivity().finish();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        chargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(UPayPaymentFragment.newInstance());
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!userManager.hasCacahedUserProfile()) {
            Log.w(TAG, "user profile missing, may be a bug when cache user profile?");
        } else {
            User user = userManager.getCurrentUser();
            titleBar.setTitle(user.getNickname());
            balanceTv.setText(user.getBalance() + " U币");
        }
        new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                titleBar.setTitle(user.getNickname());
                balanceTv.setText(user.getBalance() + " U币");
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

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
