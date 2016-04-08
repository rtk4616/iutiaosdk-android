package com.iutiao.sdk.fragments;

import android.app.Activity;
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
import com.iutiao.sdk.holders.EmailSignUpHolder;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.RegisterUserTask;

import java.util.HashMap;

public class EmailSignupFragment extends Fragment {
    EmailSignUpHolder emailSignUpHolder;

    public EmailSignupFragment() {
        // Required empty public constructor
    }

    public static EmailSignupFragment newInstance() {
        EmailSignupFragment fragment = new EmailSignupFragment();
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
        emailSignUpHolder = new EmailSignUpHolder(getActivity(),inflater.inflate(R.layout.com_iutiao_include_email_signup, container, false));
        emailSignUpHolder.goQuickSignupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(PhoneVerfyFragment.newInstance(PhoneVerfyFragment.ACTIONS.register));
            }
        });
        emailSignUpHolder.goSigninTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IUTiaoDevActivity) getActivity()).switchTo(SigninFragment.newInstance());
            }
        });
        emailSignUpHolder.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validate.isEmailValid(emailSignUpHolder.getInputEmail())){
                    emailSignUpHolder.showError(getString(R.string.com_iutiao_error_email_not_valid));
                }
                else if(TextUtils.isEmpty(emailSignUpHolder.getInputPwd())){
                    emailSignUpHolder.showError(getString(R.string.com_iutiao_error_empty_pwd));
                }
                else if(!Validate.isPwdValid(emailSignUpHolder.getInputPwd())){
                    emailSignUpHolder.showError(getString(R.string.com_iutiao_error_pwd_length));
                }
                else {
                    emailSignUpHolder.hideError();
                    performSignup();
                }
            }
        });
        return emailSignUpHolder.root;
    }

    private void performSignup() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", emailSignUpHolder.getInputEmail());
        params.put("password", emailSignUpHolder.getInputPwd());
        RegisterUserTask registerUserTask = new RegisterUserTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User t) {
                LoginManager.getInstance().onLogin(t);
                // display welcome message
                Toast.makeText(getActivity(), getResources().getString(R.string.com_iutiao_tips_welcome)+t.getNickname(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onError(Exception e) {
                emailSignUpHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }
        });
        registerUserTask.execute(params);
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
