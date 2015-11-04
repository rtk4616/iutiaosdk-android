package com.iutiao.sdk.exceptions;

/**
 * Created by yxy on 15/11/4.
 */
public class IUTiaoSdkNotInitializedException extends IUTiaoSdkException {
    static final long serialVersionUID = 1;

    public IUTiaoSdkNotInitializedException() {
        super();
    }

    public IUTiaoSdkNotInitializedException(String detailMessage) {
        super(detailMessage);
    }
}
