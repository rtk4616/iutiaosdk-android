/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.iutiao.sdk.fragments.IUTiaoDialogFragment;
import com.iutiao.sdk.fragments.LoginFragment;

public class IUTiaoActivity extends FragmentActivity {

    private static String FRAGMENT_TAG = "SingleFragment";
    private Fragment singleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_iutiao_activity_layout);

        Intent intent = getIntent();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);

        // attach fragment to activity
        if (fragment == null) {

            if (IUTiaoDialogFragment.TAG.equals(intent.getAction())) {
                IUTiaoDialogFragment dialogFragment = IUTiaoDialogFragment.newInstance();
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, FRAGMENT_TAG);
                fragment = dialogFragment;
            } else {
                fragment = LoginFragment.newInstance();
                fragment.setRetainInstance(true);
                fm.beginTransaction()
                        .add(R.id.com_iutiao_fragment_container, fragment, FRAGMENT_TAG)
                        .commit();
            }
        }

        singleFragment = fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (singleFragment != null) {
            singleFragment.onConfigurationChanged(newConfig);
        }
    }
}