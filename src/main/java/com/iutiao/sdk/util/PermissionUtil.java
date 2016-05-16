/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.iutiao.sdk.BuildConfig;

import java.util.HashMap;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;


/**
 * Created by Ben on 16/5/11.
 * 4.3－6.0的危险权限的二次确认，即使拒绝了仍会得到Granted，此为Android框架缺陷；注意对权限操作捕捉异常和数据判空等。
 */
public class PermissionUtil {
    private String TAG = "PermissionUtil";
    // TODO: 16/5/16 批量Permission申请
    private HashMap actions = new HashMap<Integer, PermissionsResultAction>();
    private Context context;

    public PermissionUtil(Context context) {
        this.context = context;
    }

    // 判断是否缺少权限(6.0)
    private boolean lacksPermission(String permission) {
        if (shouldShowRequestPermissionRationale((Activity) context, permission)) {
            Toast.makeText(context, "Please grant the permission this time", Toast.LENGTH_LONG).show();
        }
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public void askPermissionAction(Activity context,String permission, int requestCode) {
        askPermissionAction(context,permission, requestCode, null);
    }


    public void askPermissionAction(Activity context,String permission, int requestCode, PermissionsResultAction action) {
        actions.put(requestCode, action);
        if (canMakeSmores()) {
            if (lacksPermission(permission)) {
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
            } else {
                if (actions.get(requestCode) != null) {
                    ((PermissionsResultAction) actions.get(requestCode)).onGranted();
                }
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
        }

    }

    private boolean canMakeSmores() {

        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);

    }

//    // 启动应用的设置
//    private void startAppSettings(Context context) {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
//        context.startActivity(intent);
//    }

    /**
     * 启动魅族App权限页
     */
    // TODO: 16/5/11 魅族真机测试
    public void startMeiZuAppSEC() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(context.getPackageName(), BuildConfig.APPLICATION_ID);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        int grantResult = grantResults[0];
        boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            Log.i(TAG, "requestPermissions——granted" + requestCode);

            if (actions.get(requestCode) != null) {
                ((PermissionsResultAction) actions.get(requestCode)).onGranted();
            }
        } else {
            Log.i(TAG, "requestPermissions——denied");
            Toast.makeText(context, "App may not function properly without the permission\n", Toast.LENGTH_LONG).show();
            if (actions.get(requestCode) != null) {
                ((PermissionsResultAction) actions.get(requestCode)).onDenied();
            }
//            else if (魅族) {
//                跳转到魅族app安全中心;
//            } else if (华为) {
//                跳转到华为权限管理页;
//            } else if (小米) {
//                跳转到小米安全中心;
//            } else {
//                跳转到应用设置;
//            }
        }

    }

    public interface PermissionsResultAction {
        void onGranted();

        void onDenied();
    }

}
