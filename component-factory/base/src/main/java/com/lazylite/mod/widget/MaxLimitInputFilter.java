package com.lazylite.mod.widget;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import com.lazylite.mod.utils.toast.KwToast;


/**
 * @author DongJr
 * @date 2019/8/28
 */
public class MaxLimitInputFilter implements InputFilter {
    private static final int MAX_NUM = 2000;

    private int maxNum;
    private String msg;

    public MaxLimitInputFilter(){
        maxNum = MAX_NUM;
    }

    /**
     * @param maxNum 这里是字符，不是汉字（一个汉字2个字符）
     * */
    public MaxLimitInputFilter(int maxNum, String msg){
        this.maxNum = maxNum;
        this.msg = msg;
    }

    /**
     *
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end 新输入的字符串终点下标，一般为source长度-1
     * @param dest 之前输入文本框内容
     * @param dstart 表示删除后字符串的长度
     * @param dend 表示删除前的长度
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (null == source || source.length() == 0) {
            return source;
        }
        int dindex = 0;
        float count = 0;

        while (count <= maxNum && dindex < dest.length()) {
            char c = dest.charAt(dindex++);
            if (c >= 0x4E00 && c <= 0x9FA5) {
                count = count + 2f;
            } else {
                count = count + 1f;
            }
        }

        if (count > maxNum) {
            showToast();
            return source.subSequence(0, dindex - 1);
        }

        int sindex = 0;
        while (count <= maxNum && sindex < source.length()) {
            char c = source.charAt(sindex++);
            if (c >= 0x4E00 && c <= 0x9FA5) {
                count = count + 2f;
            } else {
                count = count + 1f;
            }
        }

        if (count > maxNum) {
            showToast();
            return source.subSequence(0, sindex - 1);
        }

        return source;
    }

    private void showToast(){
        if(TextUtils.isEmpty(msg)){
            return;
        }
        KwToast.show(msg);
    }

}
