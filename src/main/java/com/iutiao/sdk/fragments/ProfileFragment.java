/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.holders.ProfileHolder;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.UserProfileTask;

import java.io.IOException;
import java.net.URL;

/**
 * Created by yxy on 15/11/7.
 */

public class ProfileFragment extends BaseFragment {
    public static final UserManager userManager = UserManager.getInstance();
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ProfileHolder profileHolder;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileHolder = ProfileHolder.Create(getActivity());


        profileHolder.titleBar.setRightListener(new View.OnClickListener() {
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
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setTitle("提示"); //设置标题
                            builder.setMessage("退出登录?"); //设置内容
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); //关闭dialog
                                    LoginManager.getInstance().logOut();
                                    getActivity().finish();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        profileHolder.chargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(UPayPaymentFragment.newInstance());
            }
        });
        handler = new Handler();
//        loadAdsImage();
        return profileHolder.root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!userManager.hasCacahedUserProfile()) {
            Log.w(TAG, "user profile missing, may be a bug when cache user profile?");
        } else {
            User user = userManager.getCurrentUser();
            profileHolder.titleBar.setTitle(user.getNickname());
            profileHolder.balanceTv.setText(user.getBalance() + getString(R.string.com_iutiao_balance_suffixes));
        }
        new UserProfileTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                profileHolder.titleBar.setTitle(user.getNickname());
                profileHolder.balanceTv.setText(user.getBalance() + " U币");
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                profileHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                profileHolder.dismissProgress();
            }
        }).execute();
    }

    private void loadAdsImage(final String url) {
        handler.post(new Runnable() {
            public void run() {
                Drawable drawable = null;
                try {
                    drawable = Drawable.createFromStream(new URL(url).openStream(), "image.png");
                } catch (IOException e) {
                }
                profileHolder.adsIv.setImageDrawable(drawable);
            }
        });
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
