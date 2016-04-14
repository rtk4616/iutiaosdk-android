package com.iutiao.sdk.holders;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import com.iutiao.sdk.R;

/**
 * Created by macbookair on 16/4/1.
 */
public abstract class IUTViewHolder {
    public View root;
    public Context context;
    private ProgressDialog progressBar;
    private String progressMessage;

    public IUTViewHolder(Context context, View root) {
        this.root = root;
        this.context = context;
        this.progressBar = new ProgressDialog(context);
        setProgressMessage(this.context.getString(R.string.com_iutiao_register_phone_message));
        onHolderCreate(root);
    }

    abstract void onHolderCreate(View root);

    public void hide() {
        root.setVisibility(View.GONE);
    }

    public void show() {
        root.setVisibility(View.VISIBLE);
    }

    public void showProgress() {
        this.progressBar.setCancelable(false);
        this.progressBar.setMessage(this.context.getString(getProgressMessage()));
        this.progressBar.show();
    }

    public void dismissProgress() {
        this.progressBar.dismiss();
    }
    public void setProgressMessage(String msg){
        progressMessage = msg;
    }
    public int getProgressMessage() {
        return R.string.com_iutiao_progress_loading;
    }
    public abstract void showError(String errMsg);

    public abstract void hideError();
}
