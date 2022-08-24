package com.lazylite.mod.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.example.basemodule.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 获取打包写入Assets中的渠道信息，包括渠道名-生成apk包的系统时间
 *
 * @author LiTiancheng 2015/1/8.
 */
public class KwChannelInfoUtils {

    private static final String FILE = "channel_info";
    private static final String SPLIT_REGULAR = "-";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取渠道信息
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        String channelInfo = getChannelInfo(context);
        if (TextUtils.isEmpty(channelInfo)) {
            return "kw_cs";
        }
        String[] infos = channelInfo.split(SPLIT_REGULAR);
        if (infos.length > 1) {
            String chanel = channelInfo.split(SPLIT_REGULAR)[0];
            if (TextUtils.isEmpty(chanel)) {
                return "kw_cs";
            }
            return chanel;
        } else {
            return "kw_cs";
        }
    }

    /**
     * 这里返回不用头条分包SDK获取的渠道号(真实渠道号)
     */
    public static String getRealChannel(Context context) {
        String channelInfo = getChannelInfo(context);
        if (TextUtils.isEmpty(channelInfo)) {
            return "kw_cs";
        }
        String[] infos = channelInfo.split(SPLIT_REGULAR);
        if (infos.length > 1) {
            String chanel = channelInfo.split(SPLIT_REGULAR)[0];
            if (TextUtils.isEmpty(chanel)) {
                return "kw_cs";
            }
            return chanel;
        } else {
            return "kw_cs";
        }
    }

    /**
     * 获取打包时间
     *
     * @param context
     * @return
     */
    public static String getPackageTime(Context context) {
        String channelInfo = getChannelInfo(context);
        if (TextUtils.isEmpty(channelInfo)) {
            return "";
        }
        String[] infos = channelInfo.split(SPLIT_REGULAR);
        if (infos.length > 1) {
            return formatDate(Long.parseLong(channelInfo.split(SPLIT_REGULAR)[1]));
        } else {
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String formatDate(long time) {
        Date nowTime = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(nowTime);
    }


    public static String getVersionInfo(Context context, String path) {
        String channelInfo = "";
        try {
            InputStream is = context.getResources().getAssets().open(path);
            channelInfo = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelInfo;
    }

    private static String getChannelInfo(Context context) {
        return getVersionInfo(context, FILE);
    }

    private static String convertStreamToString(InputStream is) {
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            int len;
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer.toString();
    }

    /**
     * 信息流定制包根据渠道号区分，需要在首次安装时自动播放歌曲
     * xxx_albyy
     * xxx为渠道号，yy为id
     */
    private static List<String> autoPlayChannels = Arrays.asList(
            "tchwz",
            "jrtt",
            "wifi",
            "gdt",
            "qtt",
            "jsyytt",
            "jslctt",
            "jswztt",
            "jswifi",
            "jssogou",
            "jsqytuia",
            "kuaishou"
    );

    /**
     * 有些定制包要求接入TalkingData统计,这里只处理定制包，XXX_artXXX这一类，其他一律返回false
     */
    public static boolean isChannelNeedInstallTD(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return false;
        }
        List<String> channels = new ArrayList<>(autoPlayChannels);
//		// 移除酷比渠道
//		channels.remove("220");
        String[] infos = channel.split("_");
        if (infos.length < 2) {
            return false;
        }
        return containsChannel(channel, channels);
    }

    /**
     * 传入的channel中包含list中任意一个item即返回true
     */
    private static boolean containsChannel(String channel, List<String> list) {
        if (TextUtils.isEmpty(channel) || list == null || list.isEmpty()) {
            return false;
        }
        for (String item : list) {
            if (channel.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
