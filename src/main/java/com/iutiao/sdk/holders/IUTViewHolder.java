package com.iutiao.sdk.holders;

import android.content.Context;
import android.view.View;

/**
 * Created by macbookair on 16/4/1.
 */
public abstract class IUTViewHolder {
    public View root;
    public Context context;

    public IUTViewHolder(Context context, View root) {
        this.root = root;
        this.context = context;
        onHolderCreate(root);
    }

    abstract void onHolderCreate(View root);

    public void Hide() {
        root.setVisibility(View.GONE);
    }

    public void Show() {
        root.setVisibility(View.VISIBLE);
    }

    public abstract void showError(String errMsg);

    public abstract void hideError();
}
