/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.status;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 收集手机全局崩溃时的exception
 *
 * @author Ben
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String PARAM_PKG_NAME = "PARAM_PKG_NAME";
    private static final String PARAM_VERSION_NAME = "PARAM_VERSION_NAME";
    private static final String PARAM_VERSION_CODE = "PARAM_VERSION_CODE";
    private static final String PARAM_BOARD = "PARAM_BOARD";//主板
    private static final String PARAM_BOOTLOADER = "PARAM_BOOTLOADER";//启动加载器
    private static final String PARAM_BRND = "PARAM_BRND";//android系统定制商
    private static final String PARAM_DEVICE = "PARAM_DEVICE";// 设备参数
    private static final String PARAM_HARDWARE = "PARAM_HARDWARE"; // 显示屏参数
    private static final String PARAM_DISPLAY = "PARAM_DISPLAY"; // 显示屏参数
    private static final String PARAM_FINGERPRIINT = "PARAM_FINGERPRIINT";// 硬件名称
    private static final String PARAM_RADIOVERSION = "PARAM_RADIOVERSION";
    private static final String PARAM_RADIO = "PARAM_RADIO";
    private static final String PARAM_MANUFACTURER = "PARAM_MANUFACTURER";// 硬件制造商
    private static final String PARAM_ID = "PARAM_ID";// 修订版本列表
    private static final String PARAM_MODEL = "PARAM_MODEL";// 版本
    private static final String PARAM_PRODUCT = "PARAM_PRODUCT";// 手机制造商
    private static final String PARAM_SERIAL = "PARAM_SERIAL";
    private static final String PARAM_CPU_ABI = "PARAM_CPU_ABI";// cpu指令集
    private static final String PARAM_CPU_ABI2 = "PARAM_CPU_ABI2";

    private static final String PARAM_STACK = "PARAM_STACK";//异常堆栈

    public static final String CrashFilePath = "/sdcard/xiaomai/crashlog/";// TODO: 16/5/26 保持路径
    public static final int LogFileLimit = 5;

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private CrashLogedListener mListener;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context, CrashLogedListener listener) {
        mContext = context;
        this.mListener = listener;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(1);
        }

    }


    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put(PARAM_VERSION_NAME, versionName);
                infos.put(PARAM_VERSION_CODE, versionCode);
                infos.put(PARAM_PKG_NAME, ctx.getPackageName());
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        infos.put(PARAM_BOARD, Build.BOARD);
        infos.put(PARAM_BOOTLOADER, Build.BOOTLOADER);
        infos.put(PARAM_BRND, Build.BRAND);
        infos.put(PARAM_DEVICE, Build.DEVICE);
        infos.put(PARAM_DISPLAY, Build.DISPLAY);
        infos.put(PARAM_FINGERPRIINT, Build.FINGERPRINT);
        infos.put(PARAM_RADIOVERSION, Build.getRadioVersion());
        infos.put(PARAM_HARDWARE, Build.HARDWARE);
        infos.put(PARAM_ID,Build.ID);
        infos.put(PARAM_MANUFACTURER, Build.MANUFACTURER);
        infos.put(PARAM_MODEL, Build.MODEL);
        infos.put(PARAM_PRODUCT, Build.PRODUCT);
        infos.put(PARAM_SERIAL, Build.SERIAL);
        infos.put(PARAM_CPU_ABI, Build.CPU_ABI);
        infos.put(PARAM_CPU_ABI2, Build.CPU_ABI2);
        infos.put(PARAM_RADIO, Build.RADIO);
    }

    /**
     * 保存错误信息到键值对中
     *
     * @param ex
     * @return
     */
    private Map<String, String> saveCrashInfo2Map(Throwable ex) {

        //递归获取全部的exception信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        infos.put(PARAM_STACK, result);
        if (mListener != null) {
            mListener.afterSaveCrash(mContext,infos);
        }
        return infos;
    }

    Comparator<File> newfileFinder = new Comparator<File>() {

        @Override
        public int compare(File x, File y) {
            // TODO Auto-generated method stub
            if (x.lastModified() > y.lastModified()) return 1;
            if (x.lastModified() < y.lastModified()) return -1;
            else return 0;
        }

    };

    private int cleanLogFileToN(String dirname) {
        File dir = new File(dirname);
        if (dir.isDirectory()) {
            File[] logFiles = dir.listFiles();
            if (logFiles.length > LogFileLimit) {
                Arrays.sort(logFiles, newfileFinder);  //从小到大排
                //删掉N个以前的
                for (int i = 0; i < logFiles.length - LogFileLimit; i++) {
                    logFiles[i].delete();
                }
            }
        }

        return 1;
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        Log.i("crash", "call");
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
//        saveCrashInfo2File(ex);

        saveCrashInfo2Map(ex);
        return true;
    }

    private void sendCrashLog2PM(String fileName) {
        if (!new File(fileName).exists()) {
            Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;
                // 由于目前尚未确定以何种方式发送，所以先打出log日志。
                Log.i("info", s.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // 关闭流
            try {
                reader.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
