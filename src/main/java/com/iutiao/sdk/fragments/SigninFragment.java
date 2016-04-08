package com.iutiao.sdk.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.holders.SignInHolder;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.SigninTask;

import java.util.HashMap;

public class SigninFragment extends Fragment {
    private SignInHolder signInHolder;
    private Context context;

    public static SigninFragment newInstance() {
        SigninFragment fragment = new SigninFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        signInHolder = SignInHolder.Create(context);
        signInHolder.goQuickSignupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(PhoneVerfyFragment.newInstance(PhoneVerfyFragment.ACTIONS.register));
            }
        });
        signInHolder.forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(PhoneVerfyFragment.newInstance(PhoneVerfyFragment.ACTIONS.reset_password));
            }
        });
        signInHolder.userEt.setText(getActivity().getSharedPreferences("CachedEmail",Context.MODE_PRIVATE).getString("email",null));// TODO: 16/4/7 CacheUtil or SPUtil
        signInHolder.signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInHolder.pwdEt.requestFocus();
                String username = signInHolder.getSigninUsername();
                String pwd = signInHolder.getSigninPwd();
                if(Validate.isEmailValid(username)){
                    getActivity().getSharedPreferences("CachedEmail",Context.MODE_PRIVATE).edit().putString("email",username).commit();
                }
                if(TextUtils.isDigitsOnly(username)){
                    username = "+"+signInHolder.countrySelector.getSelectedCountryCode()+username;
                }
                if (!Validate.isEmailValid(username) && !Validate.isPhoneValid(username,signInHolder.countrySelector.getSelectedCountryName())) {
                    signInHolder.showError(getResources().getString(R.string.com_iutiao_error_invalid_email_or_phone));
                } else if (TextUtils.isEmpty(pwd)) {
                    signInHolder.showError("请输入密码");// TODO: 16/4/6 字符资源
                }
                else if(pwd.length()<6){
                    signInHolder.showError("密码长度至少6位");
                }else {
                    performSignin();
                    signInHolder.hideError();
                }
            }
        });

        signInHolder.userEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus&&TextUtils.isDigitsOnly(signInHolder.getSigninUsername())&&!TextUtils.isEmpty(signInHolder.getSigninUsername())){
                    signInHolder.countrySelector.setVisibility(View.VISIBLE);
                }else{
                    signInHolder.countrySelector.setVisibility(View.GONE);
                }
            }
        });
        return signInHolder.root;
    }


    public void performSignin() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", signInHolder.getSigninUsername());
        params.put("password", signInHolder.getSigninPwd());
        SigninTask task = new SigninTask(context, new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User user) {
                LoginManager.getInstance().onLogin(user);
                // display welcome message
                Toast.makeText(getActivity(), "欢迎回来，" + user.getNickname(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onError(Exception e) {
                signInHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }
        });
        task.execute(params);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}