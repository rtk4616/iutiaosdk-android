/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.net.APIResource;
import com.iutiao.sdk.AccessTokenManager;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Utility;
import com.iutiao.sdk.dialogs.LoginDialog;
import com.iutiao.sdk.dialogs.QuickRegisterDialog;
import com.iutiao.sdk.dialogs.RegisterDialog;
import com.iutiao.sdk.tasks.IUTiaoRequestTask;
import com.iutiao.sdk.tasks.RegisterUserTask;

import java.util.HashMap;

/**
 * Created by yxy on 15/11/4.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public static int DIALOG_FRAGMENT = 1;
    public static String DIALOG_FRAGMENT_TAG = "com.iutiao.login.dialog";

    private static final String TAG = "LoginFragment";
    Button quickLoginBtn;
    Button quickRegisterBtn;
    Button loginBtn;
    Button registerBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_iutiao_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // view lookups
        quickLoginBtn = (Button) view.findViewById(R.id.btn_quick_login);
        quickLoginBtn.setOnClickListener(this);

        loginBtn = (Button) view.findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);

        quickRegisterBtn = (Button) view.findViewById(R.id.btn_quick_register);
        quickRegisterBtn.setOnClickListener(this);

        registerBtn = (Button) view.findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this);
    }

    public void showDialog(DialogFragment dialog) {
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);

        if (frag != null) {
            ft.remove(frag);
        }
        ft.addToBackStack(null);
        dialog.show(ft, DIALOG_FRAGMENT_TAG);
    }

    public void showLoginDialog() {
        showDialog(LoginDialog.newInstance());
    }

    public void showRegisterDialog() {
        showDialog(RegisterDialog.newInstance());
    }

    public void showQuickRegisterDialog() {
        showDialog(QuickRegisterDialog.newInstance());
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public static RegisterUserTask newRegisterUserTask(Context context) {
        final Activity activity = (Activity) context;
        IUTiaoCallback<User> cb = new IUTiaoCallback<User>() {

            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    Intent i = new Intent();
                    i.putExtra("user", APIResource.GSON.toJson(user));
                    AccessTokenManager.getInstance().setCurrentAccessToken(user.getToken());
                    Toast.makeText(activity, "welcome " + user.getNickname(), Toast.LENGTH_SHORT).show();
                    activity.setResult(Activity.RESULT_OK, i);
                    activity.finish();
                }
            }

            @Override
            public void onError(final Exception e) {
                Log.e(TAG, "something went wrong on create guest", e);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "login failed " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                activity.setResult(Activity.RESULT_CANCELED);
                activity.finish();
            }

            @Override
            public void onCancel() {

            }
        };
        return new RegisterUserTask(activity, cb);
    }

    public void quickLogin() {
        RegisterUserTask task = newRegisterUserTask(getActivity());
        HashMap<String, Object> param = new HashMap<>();
        param.put("username", Utility.generateRandomString(16));
        param.put("password", Utility.generateRandomString(16));
        param.put("is_guest", true);
        task.execute(param);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_quick_login) {
            quickLogin();
        } else if (id == R.id.btn_quick_register) {
            showQuickRegisterDialog();
        } else if (id == R.id.btn_login) {
            showLoginDialog();
        } else if (id == R.id.btn_register) {
            showRegisterDialog();
        }
    }


}