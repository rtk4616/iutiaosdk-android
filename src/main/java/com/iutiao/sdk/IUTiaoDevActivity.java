package com.iutiao.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iutiao.sdk.util.Info;

public class IUTiaoDevActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView;
    EmailSignUpHolder emailSignUpHolder;
    PhoneSignUpHolder phoneSignUpHolder;
    SignInHolder signInHolder;

    public static Intent newIntent(Context ctx) {
        return new Intent(ctx, IUTiaoDevActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_iutiao_activity_iutiaodev);
        findViews();
        init();
    }

    private void findViews() {
        emailSignUpHolder = new EmailSignUpHolder(findViewById(R.id.ll_email_signup));
        phoneSignUpHolder = new PhoneSignUpHolder(findViewById(R.id.ll_phone_signup));
        signInHolder = new SignInHolder(findViewById(R.id.ll_signin));
        imageView = (ImageView) findViewById(R.id.icon_app);
    }

    private void init() {
//        load app icon
        Info info = new Info(this);
        imageView.setImageDrawable(info.getAppIcon(getPackageName()));
//        setListeners
        emailSignUpHolder.goQuickSignupTv.setOnClickListener(this);
        emailSignUpHolder.goSigninTv.setOnClickListener(this);
        phoneSignUpHolder.goEmailSignupTv.setOnClickListener(this);
        phoneSignUpHolder.goSigninTv.setOnClickListener(this);
        signInHolder.goQuickSigninTv.setOnClickListener(this);
        signInHolder.forgotPwd.setOnClickListener(this);
        emailSignUpHolder.signupBtn.setOnClickListener(this);
        phoneSignUpHolder.nextBtn.setOnClickListener(this);
        signInHolder.signinBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.textBtn_email_signup_go_email_signin || i == R.id.textBtn_phone_signup_go_email_signin) {
            goEmailSignin();
        }
        if (i == R.id.textBtn_email_signup_go_qick_register || i == R.id.textBtn_signin_go_qick_register) {
            goQuickSignup();
        }
        if (i == R.id.textBtn_phone_signup_go_email_signup) {
            goEmailSignup();
        }
        if (i == R.id.textBtn_signin_go_forgot_pwd) {
            goForgetPwd();
        }
        if (i == R.id.btn_signin_signin) {
            // TODO: 16/3/31 signin();
            String username = getSigninUsername();
//            Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
            if (Validate.isEmailValid(username) || Validate.isPhoneValid(username)) {
                Toast.makeText(this, "signin", Toast.LENGTH_SHORT).show();
                hideUserNameError();
            } else {
                showUserNameError();
            }
        }
        if (i == R.id.btn_phone_signup_next) {
            // TODO: 16/3/31  signup();
            String phone = getPhoneSignupPhone();
            if (Validate.isPhoneValid(phone)) {
                Toast.makeText(this, "signup", Toast.LENGTH_SHORT).show();
                hidePhoneError();
            } else {
                showPhoneError();
            }
        }
        if (i == R.id.btn_email_signup_signup) {
            // TODO: 16/3/31 signup();
            String email = getEmailSignupEmail();
            if (Validate.isEmailValid(email)) {
                Toast.makeText(this, "signup", Toast.LENGTH_SHORT).show();
                hideEmailError();
            } else {
                Toast.makeText(this, Validate.isEmailValid(email)+""+email, Toast.LENGTH_SHORT).show();
                showEmailError();
            }
        }
    }

//    public void performSignin() {
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("username", getInputUsername());
//        params.put("password", getInputPassword());
//        SigninTask task = new SigninTask(this, (IUTiaoCallback<User>) getTargetFragment());
//        task.execute(params);
//    }

    public void goQuickSignup() {
        emailSignUpHolder.root.setVisibility(View.GONE);
        signInHolder.root.setVisibility(View.GONE);
        phoneSignUpHolder.root.setVisibility(View.VISIBLE);
    }

    public void goEmailSignup() {
        emailSignUpHolder.root.setVisibility(View.VISIBLE);
        signInHolder.root.setVisibility(View.GONE);
        phoneSignUpHolder.root.setVisibility(View.GONE);
    }

    public void goEmailSignin() {
        emailSignUpHolder.root.setVisibility(View.GONE);
        signInHolder.root.setVisibility(View.VISIBLE);
        phoneSignUpHolder.root.setVisibility(View.GONE);
    }

    public void goForgetPwd() {
        Toast.makeText(this, "忘记密码", Toast.LENGTH_SHORT).show();

    }

    public void showEmailError() {
        emailSignUpHolder.errorTv.setVisibility(View.VISIBLE);
    }

    public void hideEmailError() {
        emailSignUpHolder.errorTv.setVisibility(View.GONE);
    }

    public void showPhoneError() {
        phoneSignUpHolder.errorTv.setVisibility(View.VISIBLE);
    }

    public void hidePhoneError() {
        phoneSignUpHolder.errorTv.setVisibility(View.GONE);
    }

    public void showUserNameError() {
        signInHolder.errorTv.setVisibility(View.VISIBLE);
    }

    public void hideUserNameError() {
        signInHolder.errorTv.setVisibility(View.GONE);
    }

    public String getPhoneSignupPhone() {
        return phoneSignUpHolder.phoneEt.getText().toString().trim();
    }

    public String getEmailSignupEmail() {
        return emailSignUpHolder.emailEt.getText().toString().trim();
    }

    public String getEmailSignupPwd() {
        return emailSignUpHolder.pwdEt.getText().toString().trim();
    }

    public String getSigninUsername() {
        return signInHolder.userEt.getText().toString().trim();
    }

    public String getSigninPwd() {
        return signInHolder.pwdEt.getText().toString().trim();
    }


    class PhoneSignUpHolder {
        LinearLayout root;
        EditText phoneEt;
        TextView nextBtn;
        TextView errorTv;
        TextView goSigninTv;
        TextView goEmailSignupTv;

        public PhoneSignUpHolder(View view) {
            this.root = (LinearLayout) view;
            phoneEt = (EditText) view.findViewById(R.id.et_phone_signup_phone);
            nextBtn = (TextView) view.findViewById(R.id.btn_phone_signup_next);
            errorTv = (TextView) view.findViewById(R.id.tv_phone_signup_error);
            goSigninTv = (TextView) view.findViewById(R.id.textBtn_phone_signup_go_email_signin);
            goEmailSignupTv = (TextView) view.findViewById(R.id.textBtn_phone_signup_go_email_signup);
        }
    }

    class EmailSignUpHolder {
        LinearLayout root;
        EditText emailEt;
        EditText pwdEt;
        TextView signupBtn;
        TextView errorTv;
        TextView goSigninTv;
        TextView goQuickSignupTv;

        public EmailSignUpHolder(View view) {
            this.root = (LinearLayout) view;
            emailEt = (EditText) view.findViewById(R.id.et_email_signup_email);
            pwdEt = (EditText) view.findViewById(R.id.et_email_signup_password);
            signupBtn = (TextView) view.findViewById(R.id.btn_email_signup_signup);
            errorTv = (TextView) view.findViewById(R.id.tv_email_signup_error);
            goSigninTv = (TextView) view.findViewById(R.id.textBtn_email_signup_go_email_signin);
            goQuickSignupTv = (TextView) view.findViewById(R.id.textBtn_email_signup_go_qick_register);
        }
    }

    class SignInHolder {
        LinearLayout root;
        EditText userEt;
        EditText pwdEt;
        TextView signinBtn;
        TextView errorTv;
        TextView goQuickSigninTv;
        TextView forgotPwd;

        public SignInHolder(View view) {
            this.root = (LinearLayout) view;
            userEt = (EditText) view.findViewById(R.id.et_signin_username);
            pwdEt = (EditText) view.findViewById(R.id.et_signin_password);
            signinBtn = (TextView) view.findViewById(R.id.btn_signin_signin);
            errorTv = (TextView) view.findViewById(R.id.tv_signin_error);
            goQuickSigninTv = (TextView) view.findViewById(R.id.textBtn_signin_go_qick_register);
            forgotPwd = (TextView) view.findViewById(R.id.textBtn_signin_go_forgot_pwd);
        }
    }

}
