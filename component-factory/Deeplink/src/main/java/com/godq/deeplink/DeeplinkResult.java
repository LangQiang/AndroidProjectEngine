package com.godq.deeplink;

public class DeeplinkResult {

    public static final String TYPE_OPEN = "open";
    public static final String TYPE_PLAY = "play";


    public static final int SUC = 200;
    public static final int LOST_TRUE_SUC = 201; //lost回调后返回true 则认为调用端成功处理此次路由
    public static final int ERR_SCHEME_NULL = -1000;
    public static final int ERR_SCHEME_NOT_SUPPORT = -2000;
    public static final int ERR_HOST_NOT_SUPPORT = -3000;
    public static final int ERR_EXCEPTION = -8888;

    public DeeplinkResult(String originScheme, int retCode, String msg) {
        this.originScheme = originScheme;
        this.code = retCode;
        this.msg = msg;
    }

    public DeeplinkResult() {
    }

    public String type;

    public int code = -1;

    public String originScheme;

    public String msg;


    //以下参数只有在成功后有意义
    public boolean isDegrade = false;

    public String finalRoutePath;

    @Override
    public String toString() {
        return "DeeplinkResult{" +
                "type='" + type + '\'' +
                ", code=" + code +
                ", srcScheme='" + originScheme + '\'' +
                ", msg='" + msg + '\'' +
                ", isDegrade=" + isDegrade +
                ", finalRoutePath='" + finalRoutePath + '\'' +
                '}';
    }
}
