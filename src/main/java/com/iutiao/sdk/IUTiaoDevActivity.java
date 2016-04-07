package com.iutiao.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.iutiao.sdk.fragments.EmailSignupFragment;


public class IUTiaoDevActivity extends AppCompatActivity {
    ImageView imageView;

    public static Intent newIntent(Context ctx) {
        return new Intent(ctx, IUTiaoDevActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_iutiao_activity_iutiaodev);
        init();
    }
    public void switchTo(Fragment fragment){
        fragment.setRetainInstance(true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();         //用Fragment动态代替布局文件中内容
        transaction.replace(R.id.sign_group, fragment);
        transaction.commit();
    }

    private void init() {
        switchTo(EmailSignupFragment.newInstance());
//        load app icon
//        imageView = (ImageView) findViewById(R.id.icon_app);
//        Info info = new Info(this);
//        imageView.setImageDrawable(info.getAppIcon(getPackageName()));
    }
}
