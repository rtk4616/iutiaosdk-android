package com.iutiao.sdk;

import java.util.Random;

/**
 * Created by yxy on 15/11/4.
 */
public final class Utility {

    static String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateRandomString(int length) {
        Random rand = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<length; i++) {
            int idx = Math.abs((rand.nextInt() % alphanumeric.length()));
            buffer.append(alphanumeric.charAt(idx));
        }
        return buffer.toString();
    }

}
