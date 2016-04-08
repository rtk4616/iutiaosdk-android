/*
 * Copyright (c) 2015, prchance, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by PRCHANCE.
 *
 */

package com.iutiao.sdk.util;

/**
 * Created by yxy on 15/12/3.
 */
public class IUTTextUtils {

    public static String nullIfBlank(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
