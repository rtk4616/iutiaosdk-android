/*
 * Copyright (c) 2016, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iutiao.sdk.R;

public class IUTTitleBar extends RelativeLayout {
    private Context mContext;
    private View rootView;
    private TextView leftTv;
    private TextView rightTv;
    private TextView titleTv;
    private String title;
    private boolean isHideLeft;
    private boolean isHideRight;
    private Drawable leftDrawable;
    private Drawable rightDrawable;


    public IUTTitleBar(Context context, WindowManager.LayoutParams param) {
        super(context);
        mContext = context;
    }

    public IUTTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IUTTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(R.layout.com_iutiao_view_iut_title_bar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IUTTitleBar);
        title = a.getString(R.styleable.IUTTitleBar_iut_title);
        isHideLeft = a.getBoolean(R.styleable.IUTTitleBar_hide_left, false);
        isHideRight = a.getBoolean(R.styleable.IUTTitleBar_hide_right, false);
        leftDrawable = a.getDrawable(R.styleable.IUTTitleBar_left_drawable);
        rightDrawable = a.getDrawable(R.styleable.IUTTitleBar_right_drawable);

        leftTv = (TextView) rootView.findViewById(R.id.tv_left);
        rightTv = (TextView) rootView.findViewById(R.id.tv_right);
        titleTv = (TextView) rootView.findViewById(R.id.tv_title);

        if(title!=null){
            setTitle(title);
        }
        if (isHideLeft) {
            leftTv.setVisibility(View.GONE);
        }
        if (isHideRight) {
            rightTv.setVisibility(View.GONE);
        }
        if(leftDrawable!=null){
            setLeftDrawable(leftDrawable);
        }
        if(rightDrawable!=null){
            setRightDrawable(rightDrawable);
        }

        setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });
    }

    public void setLeftListener(OnClickListener listener) {
        leftTv.setOnClickListener(listener);
    }

    public void setRightListener(OnClickListener listener) {
        rightTv.setOnClickListener(listener);
    }

    public void setLeftDrawable(Drawable drawable) {
        leftTv.setBackground(drawable);
    }

    public void setRightDrawable(Drawable drawable) {
        rightTv.setBackground(drawable);
    }

    public void setTitle(String title) {
        this.title = title;
        titleTv.setText(title);
    }
}