package com.lazylite.mod.utils;

import android.text.Spanned;

import com.lazylite.mod.utils.toast.KwToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qyh
 * @date 2022/1/21
 * describe:输入过滤，不支持表情
 */
public class InputEmojiFilter implements android.text.InputFilter {
    Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            KwToast.show("不支持输入表情哦");
            return "";
        }
        return null;
    }
}
