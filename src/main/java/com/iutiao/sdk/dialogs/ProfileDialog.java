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
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.tasks.UserProfileTask;

/**
 * Created by yxy on 15/11/9.
 */
public class ProfileDialog extends DialogFragment {

    private EditText nickname;
    private EditText uid;
    private TextView wallet;

    public static ProfileDialog newInstance() {
        return new ProfileDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_dialog_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nickname = (EditText) view.findViewById(R.id.et_nickname);
        uid = (EditText) view.findViewById(R.id.et_uid);

        User user = UserManager.getInstance().getCurrentUser();
        nickname.setText(user.getNickname());
        uid.setText(user.getUid());
    }


}
