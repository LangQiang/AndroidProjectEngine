package com.lazylite.mod.permission;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 将{@link Manifest.permission}转换成用户识别的文字描述。
 * <p/>
 * Created by lizhaofei on 2018/3/22 11:04
 */
public class TransformText {
    //这里应该写全部的权限
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    public static final String CAMERA = "android.permission.CAMERA";

    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    public static final String USE_SIP = "android.permission.USE_SIP";
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final String WRITE_SETTING = android.Manifest.permission.WRITE_SETTINGS;
    public static final String SYSTEM_ALERT_WINDOW =
            android.Manifest.permission.SYSTEM_ALERT_WINDOW;

    public static final String READ_PHONE_STATE_STR = "设备信息";

    /**
     * Turn permissions into text.
     */
    public static List<String> transformText(String[] permissions) {
        List<String> textList = new ArrayList<>();
        for (String permission : permissions) {
            String message = transform(permission);
            if (!textList.contains(message)) {
                textList.add(message);
            }
        }
        return textList;
    }

    public static String transform(String permission) {
        String result = "未知";
        switch (permission) {
            case READ_CALENDAR:
            case WRITE_CALENDAR: {
                result = "日历";
                break;
            }

            case CAMERA: {
                result = "相机";
                break;
            }
            case READ_CONTACTS:
            case WRITE_CONTACTS:
            case GET_ACCOUNTS: {
                result = "通讯录";
                break;
            }
            case ACCESS_FINE_LOCATION:
            case ACCESS_COARSE_LOCATION: {
                result = "位置信息";
                break;
            }
            case RECORD_AUDIO: {
                result = "麦克风";
                break;
            }
            case READ_PHONE_STATE: {
                result = READ_PHONE_STATE_STR;
                break;
            }
            case CALL_PHONE:
            case READ_CALL_LOG:
            case WRITE_CALL_LOG:
            case USE_SIP:
            case PROCESS_OUTGOING_CALLS: {
                result = "电话";
                break;
            }
            case BODY_SENSORS: {
                result = "身体传感器";
                break;
            }
            case READ_EXTERNAL_STORAGE:
            case WRITE_EXTERNAL_STORAGE: {
                result = "存储空间";
                break;
            }
            case SYSTEM_ALERT_WINDOW: {
                result = "显示悬浮窗";
                break;
            }
            case WRITE_SETTING: {
                result = "修改系统设置";
                break;
            }
        }
        if (!READ_PHONE_STATE.equals(permission)
                && !READ_EXTERNAL_STORAGE.equals(permission)
                && !WRITE_EXTERNAL_STORAGE.equals(permission)
                && !ACCESS_FINE_LOCATION.equals(permission)
                && !ACCESS_COARSE_LOCATION.equals(permission)) {
            result += "权限";
        }
        return result;
    }


    public static String getPermissionTip(String[] permissions) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasValue = false;
        for (String permission : permissions) {
            String message = getPermissionTip(permission);
            if (!TextUtils.isEmpty(message) && !stringBuilder.toString().contains(message)) {
                stringBuilder.append(message).append("和");
                hasValue = true;
            }
        }
        if (hasValue) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        if (stringBuilder.indexOf("正常使用元惜功能") != -1) {
            return "正常使用元惜功能";
        }
        return stringBuilder.toString();
    }

    public static String getPermissionTip(String permission) {
        String result = "正常使用元惜功能";
        switch (permission) {
            case READ_CALENDAR:
            case WRITE_CALENDAR: {
                break;
            }

            case CAMERA: {
                result = "上传头像正常使用";
                break;
            }
            case READ_CONTACTS:
            case WRITE_CONTACTS:
            case GET_ACCOUNTS: {
                result = "读取通讯录好友的功能正常使用";
                break;
            }
            case ACCESS_FINE_LOCATION:
            case ACCESS_COARSE_LOCATION: {
                result = "可以获取位置信息";
                break;
            }
            case RECORD_AUDIO: {
                result = "录音和连麦功能的正常使用";
                break;
            }
            case READ_PHONE_STATE: {
                break;
            }
            case CALL_PHONE:
            case READ_CALL_LOG:
            case WRITE_CALL_LOG:
            case USE_SIP:
            case PROCESS_OUTGOING_CALLS: {
                break;
            }
            case BODY_SENSORS: {
                break;
            }
            case READ_EXTERNAL_STORAGE:
            case WRITE_EXTERNAL_STORAGE: {
                result = "上传头像、或是下载图片、视频到本机的正常使用";
                break;
            }
            case SYSTEM_ALERT_WINDOW: {
                result = "桌面歌词的正常使用";
                break;
            }
            case WRITE_SETTING: {
                result = "设置铃声功能的正常使用";
                break;
            }
        }
        return result;
    }
}
