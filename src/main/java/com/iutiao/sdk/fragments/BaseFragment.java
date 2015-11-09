/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.iutiao.model.User;
import com.iutiao.sdk.IFragment;
import com.iutiao.sdk.IUTiaoCallback;

/**
 * Created by yxy on 15/11/9.
 */
public abstract class BaseFragment extends Fragment implements IFragment {

    public static int DIALOG_FRAGMENT = 1;
    public static String DIALOG_FRAGMENT_TAG = "com.iutiao.login.dialog";

    public void showDialog(DialogFragment dialog) {
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);


        if (frag != null) {
            ft.remove(frag);
            Log.i("BaseFragment", "prev fragment " + frag + " will be dismissed");
            ((DialogFragment) frag).dismiss();
        }
//        ft.addToBackStack(null);
        dialog.show(ft, DIALOG_FRAGMENT_TAG);
    }

}
