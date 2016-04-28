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
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.UserManager;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.holders.SimpleInputHolder;
import com.iutiao.sdk.tasks.BindEmailTask;

import java.util.HashMap;
import java.util.Map;

public class BindEmailFragment extends Fragment {

    private SimpleInputHolder simpleInputHolder;
    private BindEmailTask task;

    public BindEmailFragment() {
        // Required empty public constructor
    }

    public static BindEmailFragment newInstance() {
        BindEmailFragment fragment = new BindEmailFragment();
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
        simpleInputHolder = new SimpleInputHolder(getActivity(), inflater.inflate(R.layout.com_iutiao_fragment_bind_email, container, false));
        simpleInputHolder.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validate.isEmailValid(simpleInputHolder.getInput())) {
                    simpleInputHolder.showError(getString(R.string.com_iutiao_error_email_not_valid));
                    return;
                }
                bindEmail();
            }
        });
        return simpleInputHolder.root;
    }

    private void bindEmail() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", UserManager.getInstance().getCurrentUser().getUid());
        params.put("email", simpleInputHolder.getInput());
        task = new BindEmailTask(getActivity(), new IUTiaoCallback<OKEntity>() {
            @Override
            public void onSuccess(OKEntity t) {
                Toast.makeText(getActivity(), R.string.com_iutiao_tips_verify_email, Toast.LENGTH_LONG).show();
//                LoginManager.getInstance().logOut();
                User user = UserManager.getInstance().getCurrentUser();
                user.setEmail_verified(true);
                UserManager.getInstance().setCurrentUser(user);
                ((IUTiaoDevActivity) getActivity()).switchTo(AccountSettingsFragment.newInstance());
            }

            @Override
            public void onError(Exception e) {
                simpleInputHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                simpleInputHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                simpleInputHolder.dismissProgress();
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
