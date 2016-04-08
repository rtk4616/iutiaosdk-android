package com.iutiao.sdk.holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iutiao.sdk.R;
import com.iutiao.sdk.views.CountdownButton;
import com.iutiao.sdk.views.spinner.IUTCountrySelector;
import com.iutiao.sdk.views.spinner.utils.DeviceUtils;

public class PhoneVerifyHolder extends IUTViewHolder {
    public EditText phoneEt;
    public IUTCountrySelector countryST;
    public EditText codeEt;
    public EditText pwdEt;
    public TextView nextBtn;
    public TextView phoneTv;
    public TextView confirmBtn;
    public TextView errorTv;
    public TextView goSigninTv;
    public TextView verifyTipTv;
    public TextView goEmailSignupTv;
    public CountdownButton countdownTv;
    public View phoneGroup;
    public View codeGroup;

    private PhoneVerifyHolder(Context context, View view) {
        super(context,view);
    }

    public static PhoneVerifyHolder Create(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        PhoneVerifyHolder singInHolder = new PhoneVerifyHolder(context,inflater.inflate(R.layout.com_iutiao_include_phone_verfy, null, false));
        return singInHolder;
    }

    private void findViews(View root) {
        phoneEt = (EditText) root.findViewById(R.id.et_verfy_phone);
        countryST = (IUTCountrySelector) root.findViewById(R.id.countrySelector);
        codeEt = (EditText) root.findViewById(R.id.et_verfy_code);
        pwdEt = (EditText) root.findViewById(R.id.et_pwd);
        nextBtn = (TextView) root.findViewById(R.id.btn_verfy_next);
        phoneTv = (TextView) root.findViewById(R.id.tv_phone);
        errorTv = (TextView) root.findViewById(R.id.tv_verfy_error);
        countdownTv = (CountdownButton) root.findViewById(R.id.countBtn_resend);
        confirmBtn = (TextView) root.findViewById(R.id.btn_verfy_confirm);
        goSigninTv = (TextView) root.findViewById(R.id.textBtn_phone_signup_go_email_signin);
        goEmailSignupTv = (TextView) root.findViewById(R.id.textBtn_phone_signup_go_email_signup);
        verifyTipTv = (TextView) root.findViewById(R.id.tv_verfy_tip);
        phoneGroup = root.findViewById(R.id.ll_group_phone);
        codeGroup = root.findViewById(R.id.ll_group_code);
    }

    public String getNationPhone() {
        if(TextUtils.isDigitsOnly(phoneEt.getText())){
            return ("+" + countryST.getSelectedCountryCode() + phoneEt.getText().toString()).trim();
        }else{
            return phoneEt.getText().toString();
        }

    }

    public String getNationName() {
        return countryST.getSelectedCountryName();
    }

    public String getCode() {
        return codeEt.getText().toString().trim();
    }

    public String getPwd() {
        return pwdEt.getText().toString().trim();
    }

    @Override
    void onHolderCreate(View root) {
        findViews(root);
        String s = DeviceUtils.getNativePhoneNumber(context);
        if(!TextUtils.isEmpty(s)){
            phoneEt.setText(DeviceUtils.getNativePhoneNumber(context));
            countryST.setVisibility(View.GONE);
        }
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


    public void next() {
        phoneGroup.setVisibility(View.GONE);
        codeGroup.setVisibility(View.VISIBLE);
    }
}