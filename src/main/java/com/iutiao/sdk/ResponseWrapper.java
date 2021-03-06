/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk;

import com.iutiao.exception.UTiaoException;
import com.iutiao.model.UTiaoObject;

/**
 * Created by yxy on 15/12/3.
 */

public class ResponseWrapper<T extends UTiaoObject> {
    public T model;
    public UTiaoException exception;

    public ResponseWrapper(T model, UTiaoException exception) {
        this.model = model;
        this.exception = exception;
    }
}