package com.iutiao.sdk.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.spinner.IUTCountrySelector;

public class SignInHolder extends IUTViewHolder {
    public IUTCountrySelector countrySelector;
    public EditText userEt;
    public EditText pwdEt;
    public TextView signinBtn;
    public TextView errorTv;
    public TextView goQuickSignupTv;
    public TextView forgotPwd;

    private SignInHolder(Context context,View view) {
        super(context, view);
    }

    public static SignInHolder Create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        SignInHolder singInHolder = new SignInHolder(context,inflater.inflate(R.layout.com_iutiao_include_signin, null, false));
        return singInHolder;
    }

    private void findViews(View root) {
        countrySelector = (IUTCountrySelector) root.findViewById(R.id.countrySelector);
        userEt = (EditText) root.findViewById(R.id.et_signin_username);
        pwdEt = (EditText) root.findViewById(R.id.et_signin_password);
        signinBtn = (TextView) root.findViewById(R.id.btn_signin_signin);
        errorTv = (TextView) root.findViewById(R.id.tv_signin_error);
        goQuickSignupTv = (TextView) root.findViewById(R.id.textBtn_signin_go_qick_register);
        forgotPwd = (TextView) root.findViewById(R.id.textBtn_signin_go_forgot_pwd);
    }

    public String getSigninUsername() {
        return userEt.getText().toString().trim();
    }

    public String getSigninPwd() {
        return pwdEt.getText().toString().trim();
    }

    @Override
    void onHolderCreate(View root) {
        findViews(root);
    }

    @Override
    public void showError(String errMsg) {
        errorTv.setText(errMsg);
        errorTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        errorTv.setVisibility(View.GONE);
    }
}