/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by yxy on 15/11/4.
 */
public class IUTiaoDialogFragment extends DialogFragment {
    public static final String TAG = "IUTiaoDialogFragment";
    private Dialog dialog;

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.dialog == null) {
            final FragmentActivity activity = getActivity();
            Intent intent = activity.getIntent();

        }
    }

    public static IUTiaoDialogFragment newInstance() {
        return new IUTiaoDialogFragment();
    }
}
