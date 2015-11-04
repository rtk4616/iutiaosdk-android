/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.Utility;

/**
 * Created by yxy on 15/11/4.
 */
public class QuickRegisterDialog extends DialogFragment {

    private TextView phonenumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_dialog_quick_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phonenumber = (TextView) view.findViewById(R.id.et_phone);
        String myPhoneNumber = Utility.getMyPhoneNumber();
        if (myPhoneNumber != null) {
            phonenumber.setText(myPhoneNumber);
        }
    }

    public static QuickRegisterDialog newInstance() {
        return new QuickRegisterDialog();
    }

}
