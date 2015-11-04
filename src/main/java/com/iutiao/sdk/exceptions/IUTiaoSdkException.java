package com.iutiao.sdk.exceptions;

/**
 * Created by yxy on 15/11/4.
 */
public class IUTiaoSdkException extends RuntimeException {
    static final long serialVersionUID = 1;

    public IUTiaoSdkException() {
        super();
    }

    /**
     * 构建一个新的异常
     * @param detailMessage
     */
    public IUTiaoSdkException(String detailMessage) {
        super(detailMessage);
    }

    public IUTiaoSdkException(String format, Object... args) {
        this(String.format(format, args));
    }

    public IUTiaoSdkException(String message, Throwable e) {
        super(message, e);
    }

    public IUTiaoSdkException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
