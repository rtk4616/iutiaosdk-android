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

import com.iutiao.model.OKEntity;
import com.iutiao.model.User;
import com.iutiao.sdk.IUTiaoCallback;
import com.iutiao.sdk.IUTiaoDevActivity;
import com.iutiao.sdk.R;
import com.iutiao.sdk.Validate;
import com.iutiao.sdk.holders.PhoneVerifyHolder;
import com.iutiao.sdk.login.LoginManager;
import com.iutiao.sdk.tasks.RegisterPhoneTask;
import com.iutiao.sdk.tasks.ResetPasswordTask;
import com.iutiao.sdk.tasks.SMSTask;

import java.util.HashMap;
import java.util.Map;

public class PhoneVerfyFragment extends Fragment {
    private PhoneVerifyHolder phoneVerfyHolder;
    private Context context;
    private String action;
    private String receiver;

    public enum ACTIONS {
        register,
        reset_password,
        bind_phone,;
    }


    public PhoneVerfyFragment() {
        // Required empty public constructor
    }

    public static PhoneVerfyFragment newInstance(ACTIONS action) {
        Bundle args = new Bundle();
        args.putString("action", action.name());
        PhoneVerfyFragment fragment = new PhoneVerfyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PhoneVerfyFragment newRegister() {
        return PhoneVerfyFragment.newInstance(ACTIONS.register);
    }

    public static PhoneVerfyFragment newBindPhone() {
        return PhoneVerfyFragment.newInstance(ACTIONS.bind_phone);
    }

    public static PhoneVerfyFragment newResetPwd() {
        return PhoneVerfyFragment.newInstance(ACTIONS.reset_password);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        action = getArguments().getString("action");
        Validate.isInEnum(action, ACTIONS.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        phoneVerfyHolder = PhoneVerifyHolder.Create(context);
        if (action.equals(ACTIONS.reset_password.name())) {
            phoneVerfyHolder.pwdEt.setVisibility(View.VISIBLE);
            phoneVerfyHolder.verifyTipTv.setVisibility(View.GONE);
            phoneVerfyHolder.goSigninTv.setVisibility(View.GONE);
            phoneVerfyHolder.goEmailSignupTv.setVisibility(View.GONE);
        } else if (action.equals(ACTIONS.bind_phone.name())) {
            phoneVerfyHolder.nextBtn.setText(R.string.com_iutiao_verify_bind_phone);
            phoneVerfyHolder.verifyTipTv.setVisibility(View.GONE);
            phoneVerfyHolder.goSigninTv.setVisibility(View.GONE);
            phoneVerfyHolder.goEmailSignupTv.setVisibility(View.GONE);
        } else {
            phoneVerfyHolder.nextBtn.setText(getString(R.string.com_iutiao_next));
            phoneVerfyHolder.verifyTipTv.setVisibility(View.VISIBLE);
            phoneVerfyHolder.goSigninTv.setVisibility(View.VISIBLE);
            phoneVerfyHolder.goEmailSignupTv.setVisibility(View.VISIBLE);
            phoneVerfyHolder.goSigninTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IUTiaoDevActivity) getActivity()).switchTo(SigninFragment.newInstance());
                }
            });
            phoneVerfyHolder.goEmailSignupTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IUTiaoDevActivity) getActivity()).switchTo(EmailSignupFragment.newInstance());
                }
            });
        }
        phoneVerfyHolder.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validate.isPhoneValid(phoneVerfyHolder.getNationPhone(), phoneVerfyHolder.getNationName())) {
                    phoneVerfyHolder.showError(getString(R.string.com_iutiao_error_phone));
                } else if (action.equals(ACTIONS.register.name())) {
                    quickRegister();
                } else if (action.equals(ACTIONS.bind_phone.name())) {
                    receiver = phoneVerfyHolder.getNationPhone();
                    resendCode();
                    phoneVerfyHolder.phoneTv.setText(receiver);
                    phoneVerfyHolder.countdownTv.startCountDown();
                    phoneVerfyHolder.hideError();
                    phoneVerfyHolder.next();
                } else {
                    receiver = phoneVerfyHolder.getNationPhone();
                    resendCode();
                    phoneVerfyHolder.phoneTv.setText(receiver);
                    phoneVerfyHolder.countdownTv.startCountDown();
                    phoneVerfyHolder.hideError();
                    phoneVerfyHolder.next();

                }
            }
        });
        phoneVerfyHolder.countdownTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
        phoneVerfyHolder.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneVerfyHolder.getCode())) {
                    phoneVerfyHolder.showError(getString(R.string.com_iutiao_error_empty_verify_code));
                } else if (action.equals(ACTIONS.reset_password.name()) && TextUtils.isEmpty(phoneVerfyHolder.getPwd())) {
                    phoneVerfyHolder.showError(getString(R.string.com_iutiao_error_empty_pwd));
                } else if (action.equals(ACTIONS.reset_password.name()) && !TextUtils.isEmpty(phoneVerfyHolder.getPwd())) {
                    resetPassword();
                } else {
                }
            }
        });
        return phoneVerfyHolder.root;
    }

    private void quickRegister() {
        HashMap<String, Object> p = new HashMap<>();
        p.put("phone_number", phoneVerfyHolder.getNationPhone());
        RegisterPhoneTask task = new RegisterPhoneTask(getActivity(), new IUTiaoCallback<User>() {
            @Override
            public void onSuccess(User t) {
                LoginManager.getInstance().onLogin(t);
                Toast.makeText(getActivity(), getString(R.string.com_iutiao_tips_welcome) + t.getNickname(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onError(Exception e) {
                phoneVerfyHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                phoneVerfyHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                phoneVerfyHolder.dismissProgress();
            }
        });
        task.execute(p);
    }

    public void resendCode() {

        SMSTask task = new SMSTask(getActivity(), new IUTiaoCallback() {
            @Override
            public void onSuccess(Object t) {
                Toast.makeText(getActivity(), "request has been sent, please wait for the SMS", Toast.LENGTH_SHORT).show();
// TODO: 16/4/6 go verify page here (should go to verify page after sending code)
            }

            @Override
            public void onError(Exception e) {
                phoneVerfyHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                phoneVerfyHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                phoneVerfyHolder.dismissProgress();
            }
        });
        HashMap<String, Object> p = new HashMap<String, Object>() {{
            put("receiver", phoneVerfyHolder.getNationPhone());
            put("action", action);
        }};
        task.execute(p);
    }

    private void resetPassword() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("password", phoneVerfyHolder.getPwd());
        params.put("phone_number", phoneVerfyHolder.getNationPhone());
        params.put("code", phoneVerfyHolder.getCode());
        ResetPasswordTask task = new ResetPasswordTask(getActivity(), new IUTiaoCallback<OKEntity>() {
            @Override
            public void onSuccess(OKEntity t) {
                Toast.makeText(getActivity(), R.string.com_iutiao_tips_success_reset_pwd, Toast.LENGTH_LONG).show();
                ((IUTiaoDevActivity)getActivity()).switchTo(SigninFragment.newInstance());
            }

            @Override
            public void onError(Exception e) {
                phoneVerfyHolder.showError(e.getMessage());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onPreExecute() {
                phoneVerfyHolder.showProgress();
            }

            @Override
            public void onExecuted() {
                phoneVerfyHolder.dismissProgress();
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
