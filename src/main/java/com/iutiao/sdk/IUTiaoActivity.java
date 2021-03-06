/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.iutiao.net.RequestOptions;
import com.iutiao.sdk.fragments.IUTiaoDialogFragment;
import com.iutiao.sdk.fragments.LoginFragment;
import com.iutiao.sdk.fragments.ProfileFragment;
import com.iutiao.sdk.fragments.UPayPaymentFragment;

public class IUTiaoActivity extends FragmentActivity {

    private static String FRAGMENT_TAG = "SingleFragment";
    private Fragment singleFragment;
    private static final String TAG = IUTiaoActivity.class.getSimpleName();

    public static Intent newChargeIntent(Context ctx) {
        Intent i = newIntent(ctx);
        i.putExtra("fragment", "charge");
        return i;
    }

    public static Intent newChargeIntent(Context ctx, String appOrderId) {
        Intent i = newChargeIntent(ctx);
        i.putExtra("app_orderid", appOrderId);
        return i;
    }

    public static Intent newProfileIntent(Context ctx) {
        Intent i = newIntent(ctx);
        i.putExtra("fragment", "profile");
        return i;
    }

    public static Intent newIntent(Context ctx) {
        return new Intent(ctx, IUTiaoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置 ActionBar
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.com_iutiao_activity_layout);

        Intent intent = getIntent();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);

        // attach fragment to activity
        if (fragment == null) {

            String token = RequestOptions.getInstance().getToken();

            if (IUTiaoDialogFragment.TAG.equals(intent.getAction())) {
                IUTiaoDialogFragment dialogFragment = IUTiaoDialogFragment.newInstance();
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, FRAGMENT_TAG);
                fragment = dialogFragment;
            } else {
                if (token != null) {
                    String frag = intent.getStringExtra("fragment");
                    if (frag != null) {
                        if (frag.equals("charge")) {
//                            fragment = ChargeFragment.newInstance(getIntent());
                            //FIXME: hard code here.
                            fragment = UPayPaymentFragment.newInstance();
                        }
                    } else {
                        fragment = ProfileFragment.newInstance();
                    }
                } else {
                    fragment = LoginFragment.newInstance();
                }
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

    public static Intent newIntent(Context ctx, String fragment) {
        Intent intent = new Intent(ctx, IUTiaoActivity.class);
        intent.putExtra("fragment", fragment);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}