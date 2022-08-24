package com.lazylite.mod.utils;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenliu on 2016/8/31.<br/>
 * 描述：
 * </br>
 */
public class SpanUtils {

    public static class PatternString {
        /**
         * #号括起来的话题#
         */
        public static final String NUMBER_SIGN_PATTERN = "#[^#]+#";

        /**
         * 表情[大笑]
         */
        public static final String EXPRESSION_PATTERN = "\\[[^\\]]+\\]";

        /**
         * 网址
         */
        public static final String URL_PATTERN = "(([hH]ttp[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";

    }

    /**
     * @param <T>
     */
    public interface SpanClickListener<T> {
        void onSpanClick(T t);
    }


    /**
     * 关键词变色处理
     *
     * @param str
     * @param patterStr 需要变色的关键词 或者 正则表达式
     * @return
     */
    public static SpannableString getKeyWordSpan(int color, String str, String patterStr) throws Exception {
        SpannableString spannableString = new SpannableString(str);
        Pattern patten = Pattern.compile(patterStr, Pattern.CASE_INSENSITIVE);
        dealPattern(color, spannableString, patten, 0);
        return spannableString;
    }

    /**
     * 自动识别话题并做颜色处理,可点击
     *
     * @param color
     * @param str
     */
    public static SpannableString getNumberSignSpan(int color, String str, SpanClickListener spanClickListener, Object obj, TextView tv) throws Exception {
        SpannableString spannableString = new SpannableString(str);
        Pattern patten = Pattern.compile(PatternString.NUMBER_SIGN_PATTERN, Pattern.CASE_INSENSITIVE);
        if (spanClickListener != null) {
            dealClick(spannableString, patten, 0, spanClickListener, obj, tv);
        }
        dealPattern(color, spannableString, patten, 0);
        if (tv != null) {
            tv.setText(spannableString);
        }
        return spannableString;
    }

    public static SpannableString getNumberSignSpan(int color, String str, TextView tv) throws Exception {
        return getNumberSignSpan(color, str, null, null, tv);
    }

    public static SpannableString getActivitySpan(int color, String str, SpanClickListener spanClickListener, Object obj, TextView tv) throws Exception {
        return getNumberSignSpan(color, str, spanClickListener, obj, tv);
    }


    /**
     * 表情处理
     *
     * @param context
     * @param str
     * @return
     */
//    public static SpannableString getExpressionSpan(Context context, String str) throws Exception {
//        return ExpressionConvertUtil.getInstace().getExpressionString(context, str);
//    }


    /**
     * 对spanableString进行正则判断，如果符合要求，则将内容变色
     *
     * @param color
     * @param spannableString
     * @param patten
     * @param start
     */
    public static void dealPattern(int color, SpannableString spannableString, Pattern patten, int start) {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            //设置前景色span
            spannableString.setSpan(new ForegroundColorSpan(color), matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealPattern(color, spannableString, patten, end);
            }
            break;
        }
    }

    /*private static void dealClickAndPattern(int color, SpannableString spannableString, Pattern patten, int start, final SpanClickListener spanClickListener, final Object bean, TextView tv) throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            //设置前景色span
            spannableString.setSpan(new ForegroundColorSpan(color), matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (tv != null) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    spanClickListener.onSpanClick(bean);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置画笔属性
                    ds.setUnderlineText(false);//默认有下划线
                }
            }, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealClickAndPattern(color, spannableString, patten, end, spanClickListener, bean, tv);
            }
            break;
        }
    }*/

    /**
     * 对spanableString进行正则判断，如果符合要求，将内容设置可点击
     *
     * @param spannableString
     * @param patten
     * @param start
     * @param spanClickListener
     * @param bean
     */
    public static void dealClick(SpannableString spannableString, Pattern patten, int start, final SpanClickListener spanClickListener, final Object bean, TextView tv) {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    spanClickListener.onSpanClick(bean);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置画笔属性
                    ds.setUnderlineText(false);//默认有下划线
                }
            }, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealClick(spannableString, patten, end, spanClickListener, bean, tv);
            }
            if (tv != null) {
//                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        boolean ret = false;
                        CharSequence text = ((TextView) v).getText();
                        Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                        TextView widget = (TextView) v;
                        int action = event.getAction();
                        if (action == MotionEvent.ACTION_UP ||
                                action == MotionEvent.ACTION_DOWN) {
                            int x = (int) event.getX();
                            int y = (int) event.getY();

                            x -= widget.getTotalPaddingLeft();
                            y -= widget.getTotalPaddingTop();


                            x += widget.getScrollX();
                            y += widget.getScrollY();


                            Layout layout = widget.getLayout();
                            int line = layout.getLineForVertical(y);
                            int off = layout.getOffsetForHorizontal(line, x);
                            ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);
                            if (link.length > 0) {
                                if (action == MotionEvent.ACTION_UP && link[0]!=null) {
                                    link[0].onClick(widget);
                                }
                                ret = true;
                            }
                        }
                        return ret;
                    }
                });

            }
            break;
        }
    }

}
