/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iutiao.model.OKEntity;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.holders.ChangePwdHolder;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.ChangePasswordTask;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {

    private ChangePwdHolder changePwdHolder;
    private ChangePasswordTask task;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        changePwdHolder = new ChangePwdHolder(getActivity(), inflater.inflate(R.layout.com_iutiao_fragment_reset_password, container, false));
        changePwdHolder.title.setTitle(UserManager.getInstance().getCurrentUser().getNickname());
        changePwdHolder.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validate.isPwdValid(changePwdHolder.getInputPwd1()) || !Validate.isPwdValid(changePwdHolder.getInputPwd2())) {
                    changePwdHolder.showError(getString(R.string.com_iutiao_error_pwd_length));
                    return;
                }
                if (!changePwdHolder.getInputPwd1().equals(changePwdHolder.getInputPwd2())) {
                    changePwdHolder.showError("密码不一致");
                    return;
                }
                resetPwd();
            }
        });
        return changePwdHolder.root;
    }

    private void resetPwd() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("password1", changePwdHolder.getInputPwd1());
        params.put("password2", changePwdHolder.getInputPwd2());
        task = new ChangePasswordTask(getActivity(), new IUTiaoCallback<OKEntity>() {
            @Override
            public void onSuccess(OKEntity t) {
                Toast.makeText(getActivity(), R.string.com_iutiao_tips_success_reset_pwd, Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                ((IUTiaoDevActivity) getActivity()).switchTo(SigninFragment.newInstance());
            }

            @Override
            public void onError(Exception e) {
                changePwdHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                changePwdHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                changePwdHolder.dismissProgress();
            }
        });
        task.execute(params);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (task != null) {
            task.cancel(true);
            task= null;
        }
    }

}
