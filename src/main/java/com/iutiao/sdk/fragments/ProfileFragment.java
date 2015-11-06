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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iutiao.sdk.AccessTokenManager;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.login.LoginManager;

/**
 * Created by yxy on 15/11/7.
 */
public class ProfileFragment extends Fragment {

    private Button signOutBtn;
    private Button profileBtn;

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.com_iutiao_fragment_profile, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signOutBtn = (Button) view.findViewById(R.id.btn_signout);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                getActivity().finish();
            }
        });
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
