package com.lazylite.mod.http.okhttp;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;

class OkUtils {

    static long getFileLength(String filename) {

        if (TextUtils.isEmpty(filename)) {
            return 0;
        }
        File f = new File(filename);
        return f.length();
    }

    static void closeQuietly(Closeable... closeables) {
        for (Closeable c : closeables) {
            if (c != null)
                try {
                    c.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }
    }
}
