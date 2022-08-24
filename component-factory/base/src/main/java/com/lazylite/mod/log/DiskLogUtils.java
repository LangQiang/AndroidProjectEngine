package com.lazylite.mod.log;

import android.os.Environment;
import android.text.TextUtils;

import com.lazylite.mod.App;
import com.lazylite.mod.threadpool.KwThreadPool;
import com.lazylite.mod.utils.KwFileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author qyh
 * email：yanhui.qiao@kuwo.cn
 * @date 2021/6/7.
 * description：磁盘日志工具类
 */
public class DiskLogUtils {
    private static final String logFileName = "lazyLog";
    private static String mFilePath;

    /**
     * isWrite:是否把日志写入磁盘文件中
     */
    private static boolean isWrite = true;

    /**
     * 存放日志文件的所在路径
     */
    private static String mPath;

    static {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mPath = App.getInstance().getExternalFilesDir("lazy").getPath();
            mFilePath = mPath + "/" + logFileName + ".log";
        } else {
            isWrite = false;
        }
    }

    /**
     * 写磁盘文件的方法，每个字段自动换行
     */
    public static void writeLogByFileName(final String... contents) {
        if (!isWrite || TextUtils.isEmpty(logFileName)) {
            return;
        }

        KwThreadPool.runThread(new Runnable() {
            @Override
            public void run() {
                File root = new File(mPath);
                if (!root.exists()) {
                    root.mkdir();
                }
                writeToFile(mFilePath, contents);
            }
        });
    }

    private static void writeToFile(String filePath, String[] contents) {
        File file = new File(filePath);
        BufferedWriter out = null;
        FileWriter writer = null;
        try {
            if (file.exists()) {
                writer = new FileWriter(file, true);
            } else {
                file.createNewFile();
                writer = new FileWriter(file);
            }
            out = new BufferedWriter(writer);
            for (String line : contents) {
                out.write(line);
            }
            out.flush();
            writer.flush();
        } catch (IOException e) {
            LogMgr.e("write logs failed");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            LogMgr.e("write close failed!");
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogMgr.e("write close failed");
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    LogMgr.e("write close failed!");
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LogMgr.e("write close failed!");
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    LogMgr.e("write close failed!");
                    e.printStackTrace();
                }
            }
        }
    }


    public static void deleteCache() {
        KwThreadPool.runThread(new Runnable() {
            @Override
            public void run() {
                KwFileUtils.deleteFile(mFilePath);
            }
        });
    }


    public static void readFileContent(final OnReadFileContentListener listener) {
        KwThreadPool.runThread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mFilePath);
                BufferedReader reader = null;
                StringBuffer sbf = new StringBuffer();
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String tempStr;
                    while ((tempStr = reader.readLine()) != null) {
                        sbf.append(tempStr);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                if (listener != null) {
                    listener.onContent(sbf.toString());
                }
            }
        });
    }

    public interface OnReadFileContentListener {
        void onContent(String content);
    }
}
