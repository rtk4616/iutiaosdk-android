package com.iutiao.sdk.holders;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iutiao.sdk.R;

public class EmailSignUpHolder extends IUTViewHolder {
    public EditText emailEt;
    public EditText pwdEt;
    public TextView signupBtn;
    public TextView errorTv;
    public TextView goSigninTv;
    public TextView goQuickSignupTv;

    public EmailSignUpHolder(Context context,View root) {
        super(context, root);
    }

    @Override
    void onHolderCreate(View root) {
        findView(root);
    }

    @Override
    public void showError(String errMsg) {
        errorTv.setVisibility(View.VISIBLE);
        errorTv.setText(errMsg);
    }

    @Override
    public void hideError() {
        errorTv.setVisibility(View.GONE);
    }

    public String getInputEmail() {
        return emailEt.getText().toString().trim();
    }

    public String getInputPwd() {
        return pwdEt.getText().toString().trim();
    }

    private void findView(View root) {
        emailEt = (EditText) root.findViewById(R.id.et_email_signup_email);
        pwdEt = (EditText) root.findViewById(R.id.et_email_signup_password);
        signupBtn = (TextView) root.findViewById(R.id.btn_email_signup_signup);
        errorTv = (TextView) root.findViewById(R.id.tv_email_signup_error);
        goSigninTv = (TextView) root.findViewById(R.id.textBtn_email_signup_go_email_signin);
        goQuickSignupTv = (TextView) root.findViewById(R.id.textBtn_email_signup_go_qick_register);
    }
}