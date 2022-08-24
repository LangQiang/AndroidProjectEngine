package com.lazylite.mod.utils;

import android.text.TextUtils;

/**
 * Created by lzf on 2022/1/24 4:33 下午
 */
public class StringUtils {

    // 中文算一个字符，除中文以外的所有字符都算半个
    public static float computeCharacterLength(String text){
        if (!StringUtils.isTruelyEmpty(text)){
            return 0f;
        }
        float length = 0f;
        for (char character : text.toCharArray()){
            if (character >= 0x4E00 && character <= 0x9FA5){
                length += 1f;
            } else {
                length += 0.5f;
            }
        }
        return length;
    }

    /**
     * 判断是不是空字符串, 空白字符也是空!
     */
    public static boolean isTruelyEmpty(String s) {
        return s != null && !TextUtils.isEmpty(s.trim());
    }
}
