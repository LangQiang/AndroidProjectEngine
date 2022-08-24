package com.lazylite.mod.utils;

import java.util.Random;

public class LRSign {

    public static final String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    static{
        System.loadLibrary("lrsign");
    }

    public native static String sign(String url, String body);

    public static String getRandomString(int length){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
