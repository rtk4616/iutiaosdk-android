package com.iutiao.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.iutiao.sdk.util.Info;

public class IUTiaoDevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_iutiao_activity_iutiaodev);
        Info info = new Info(this);
        ImageView imageView = (ImageView) findViewById(R.id.icon_app);
        imageView.setImageDrawable(info.getAppIcon(getPackageName()));

    }
    public static Intent newIntent(Context ctx) {
        return new Intent(ctx, IUTiaoDevActivity.class);
    }
}
