package com.lazylite.mod.http.okhttp;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OkHttpConstants {

    public static final String TAG = "OkHttpTag";

    static final String FRAME_NAME = "okHttp";

    static final String CHILD_CACHE_FILE = "KwOkCache";

    static final int CACHE_SIZE = 10 * 1024 * 1024;

    static final int TIME_OUT_CONNECT_SECONDS = 10;

    static final int TIME_OUT_WRITE_SECONDS = 30;

    static final int TIME_OUT_READ_SECONDS = 100;

    static final int CODE_EMPTY_BODY = 700;

    static final int CODE_IO_EXCEPTION = 701;

    static final int CODE_EXCEPTION = 702;

    static final int CODE_FAILURE = 703;

    static final int CODE_OOM = 704;

    static final int CODE_HTTP_URL_BUILD_ERROR = 705;

    public static final int DOWNLOAD_CODE_PARAM_ERROR = 1001;

    public static final int DOWNLOAD_CODE_FILE_NOT_FOUND = 1002;

    public static final int DOWNLOAD_CODE_FILE_IO_ERROR = 1003;

    public static final int DOWNLOAD_CODE_EMPTY_BODY = 1004;

    public static final int DOWNLOAD_CODE_EXCEPTION_ERROR = 1005;

    public static final int DOWNLOAD_CODE_OOM_ERROR = 1006;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef (value = {
            CALLBACK_TYPE.CALLBACK_TYPE_ERROR,
            CALLBACK_TYPE.CALLBACK_TYPE_COMPLETE,
            CALLBACK_TYPE.CALLBACK_TYPE_START,
            CALLBACK_TYPE.CALLBACK_TYPE_PROGRESS
    })
    public @interface CALLBACK_TYPE {

        int CALLBACK_TYPE_ERROR = 1;

        int CALLBACK_TYPE_COMPLETE = 2;

        int CALLBACK_TYPE_START = 3;

        int CALLBACK_TYPE_PROGRESS = 4;
    }

}
