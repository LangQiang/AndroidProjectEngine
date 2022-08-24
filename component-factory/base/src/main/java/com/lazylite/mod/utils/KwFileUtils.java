package com.lazylite.mod.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.lazylite.mod.App;
import com.lazylite.mod.log.LogMgr;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzf on 5/28/21 11:22 AM
 */
public class KwFileUtils {
    private final static String		TAG					= "FileUtils";

    /**
     * 保存对象
     * appContext.saveObject( (Serializable) listMyAnswerTmp, cachKey) ;
     */
    public static boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try{
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos) ;
            oos.writeObject(ser) ;
            oos.close() ;
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception ignored) {}
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * 读取对象
     * (List<list>) appContext.readObject(cachKey);
     */
    public static Serializable readObject(String file){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try{
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            return (Serializable)ois.readObject();
        }catch(FileNotFoundException ignored){
        }catch(Exception e){
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if(e instanceof InvalidClassException){
                File data = new File(file);
                if(data.exists()) {
                    data.delete();
                }
            }
        }finally{
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception ignored) {}
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * 获取除扩展名以外的部分
     *
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutSuffix(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int lastIndex = fileName.lastIndexOf(".");
        String fileNameWithoutSuffix = "";
        if (lastIndex != -1) {
            fileNameWithoutSuffix = fileName.substring(0, lastIndex);
        }
        return fileNameWithoutSuffix;
    }

    /**
     * 获取文件扩展名
     *
     * @param path
     * @return
     */
    public static String getFileExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String extension = path.substring(index + 1, path.length());
        if (TextUtils.isEmpty(extension)) {
            return "";
        }
        return extension;
    }

    /**
     * 获取文件目录
     */
    public static String getFilePath(String fullPathName) {
        int lastIndex = fullPathName.lastIndexOf(File.separator);
        String path = "";
        if (lastIndex != -1) {
            path = fullPathName.substring(0, lastIndex);
        }
        return path;
    }

    /**
     * 获取不带路径和后缀的文件名
     *
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int separatorIndex = path.lastIndexOf(File.separator);
        if (separatorIndex > 0 && separatorIndex != path.length() - 1) {
            String fullName = path.substring(separatorIndex + 1, path.length());
            String name = getFileNameWithoutSuffix(fullName);
            return name;
        }
        return path;
    }

    /**
     * 获取不带路径的文件名
     *
     * @param path
     * @return
     */
    public static String getFullFileNameByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        int separatorIndex = path.lastIndexOf(File.separator);

        if (separatorIndex > 0 && separatorIndex != path.length() - 1) {
            String fullName = path.substring(separatorIndex + 1, path.length());
            return fullName;
        }

        return path;
    }

    // 文件改名
    public static boolean fileMove(String from, String to, boolean overwrite) {
        return fileMove(from, to, overwrite, false);
    }

    public static boolean fileMove(String from, String to, boolean overwrite, boolean copy) {
        File fromFile = new File(from);

        if (!fromFile.exists()) {
            return false;
        }

        File toFile = new File(to);
        if (toFile.exists()) {
            if (overwrite) {
                toFile.delete();
            } else {
                return false;
            }
        }

        boolean ret = false;

        if (!copy) {
            ret = fromFile.renameTo(toFile);
        }

        if (!ret) {
            ret = fileCopy(fromFile, toFile);
            if (ret) {
                deleteFile(from);
            }
        }

        return ret;
    }

    // 过滤掉不可当文件名的字符
    static public String delInvalidFileNameStr(String title) {
        if (title != null && title.length() > 0) {
            String illegal = "[`\\\\~!@#\\$%\\^&\\*+=\\|\\{\\}:;\\,/\\.<>\\?·\\s\"]";
            Pattern pattern = Pattern.compile(illegal);
            Matcher matcher = pattern.matcher(title);
            return matcher.replaceAll("_").trim();
        }

        return title;
    }

    /**
     * 纠正乱码字符串
     *
     * @param string
     * @return
     */
    public static String changeEncode(String string) {
        try {
            if (string != null && !StringCodec.isEncodable(string, "gbk")
                    && StringCodec.isEncodable(string, "iso-8859-1")) {
                return new String(string.getBytes("iso-8859-1"), "gbk");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 创建空文件
     *
     * @param path
     *            待创建的文件路径
     * @param size
     *            空文件大小
     * @return 创建是否成功
     * @throws IOException
     */
    public static boolean createEmptyFile(String path, long size) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            try {
                raf.setLength(size);
            } finally {
                raf.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     *            文件路径
     * @return 是否存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    // 拷贝文件
    public static boolean fileCopy(String from_file, String to_file) {
        return fileCopy(new File(from_file), new File(to_file));
    }

    public static boolean fileCopy(File from_file, File to_file) {
        if (!from_file.exists()) {
            return false;
        }

        if (to_file.exists()) {
            to_file.delete();
        }
        boolean success = true;
        FileInputStream from = null;
        FileOutputStream to = null;
        byte[] buffer;
        try {
            buffer = new byte[1024];
        } catch (OutOfMemoryError oom) {
            LogMgr.e(TAG, oom);
            return false;
        }
        try {
            from = new FileInputStream(from_file);
            to = new FileOutputStream(to_file); // Create output stream
            int bytes_read;

            while ((bytes_read = from.read(buffer)) != -1) {
                // Read until EOF
                to.write(buffer, 0, bytes_read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            buffer = null;
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
                from = null;
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
                to = null;
            }
        }

        if (!success) {
            to_file.delete();
        }

        return success;
    }

    public static boolean fileCopy(InputStream stream, File to_file) {
        if (stream == null) {
            return false;
        }

        if (to_file.exists()) {
            to_file.delete();
        }
        boolean success = true;
        FileOutputStream to = null;
        byte[] buffer;
        try {
            buffer = new byte[1024];
        } catch (OutOfMemoryError oom) {
            LogMgr.e(TAG, oom);
            return false;
        }
        try {
            to = new FileOutputStream(to_file); // Create output stream
            int bytes_read;
            while ((bytes_read = stream.read(buffer)) != -1) {
                // Read until EOF
                to.write(buffer, 0, bytes_read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            buffer = null;
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
                stream = null;
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
                to = null;
            }
        }

        if (!success) {
            to_file.delete();
        }

        return success;
    }

    /**
     * 删除文件或者目录
     *
     * @param path
     *            指定路径的文件或目录
     * @return 返回操作结果
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) return true;

        if (file.isDirectory()) {
            String[] subPaths = file.list();
            if (subPaths != null){ //4.4系统无权限可能为空
                for (String p : subPaths) {
                    if (!deleteFile(path + File.separator + p)) {
                        return false;
                    }
                }
            }else{
                LogMgr.e(TAG, "deleteFile-->"+path);
            }
        }

        return file.delete();
    }

    public static boolean deleteFilesExcept(String path, String fileName) {
        File[] files = getFiles(path);
        if(files != null) {
            for(File f : files) {
                if(!f.getAbsolutePath().endsWith(fileName)) {
                    f.delete();
                }
            }
        }
        return true;
    }

    /**
     * 创建目录，包括必要的父目录的创建，如果未创建
     *
     * @param path
     *            待创建的目录路径
     * @return 返回操作结果
     */
    public static boolean mkdir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return true;
        }

        return file.mkdirs();
    }

    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = null;
        try {
            stat = new StatFs(path.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = null;
        try {
            stat = new StatFs(path.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long availableSize = availableBlocks * blockSize;
        return availableSize - 5 * 1024 * 1024;// 预留5M的空间
    }

    public static boolean isExternalStorageWriterable() {
        String state = null;

        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public static long getTotalExternalMemorySize() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = null;
            try {
                stat = new StatFs(path.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }

            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return blockSize*totalBlocks;
        }

        return 0;
    }

    /**
     * 检查当前sdcard剩余空间大小
     */
    public static long getAvailableExternalMemorySize() {
        String state = null;

        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = null;
            try {
                stat = new StatFs(path.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            return availableSize - 5 * 1024 * 1024;// 预留5M的空间
        }

        return 0;
    }

    /**
     * 检查当前sdcard剩余空间大小是否够用，32M以上返回true
     */
    public static boolean isExternalSpaceAvailable() {
        return getAvailableExternalMemorySize() > 32 * 1024 * 1024;
    }

    /**
     * 获取当前目录的文件夹列表
     */
    public static ArrayList<File> getDirs(String path) {
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        fileList.add(f);
                    }
                }
            }
        }
        return fileList;
    }

    public static File[] getFiles(String path) {
        return getFiles(path, null);
    }

    /**
     * 获取当前目录的文件列表
     */
    public static File[] getFiles(String path, final String[] filters) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return null;
        }

        FilenameFilter filter = null;
        if (filters != null && filters.length > 0) {
            filter = new FilenameFilter() {
                @Override
                public boolean accept(File directory, String filename) {
                    if (!TextUtils.isEmpty(filename)) {
                        String lowerCase = filename.toLowerCase();
                        for (String type : filters) {
                            if (lowerCase.endsWith(type)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
        }

        File[] fileList = file.listFiles(filter);
        return fileList;
    }

    /**
     * 获取当前目录的文件列表，用正则匹配
     */
    public static File[] getFilesByRegex(String path, final String regex,
                                         final String exceptRegex) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(regex)) {
            return null;
        }
        File[] fileList = null;
        File file = new File(path);
        if (file.isDirectory()) {
            fileList = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File directory, String filename) {
                    if (filename != null && !"".equals(filename)) {
                        try {
                            if (filename.matches(regex)) {
                                if (exceptRegex == null || exceptRegex.length() == 0) {
                                    return true;
                                } else {
                                    return !filename.matches(exceptRegex);
                                }
                            }
                        } catch (Exception e) {
                            LogMgr.e(TAG, e);
                            return false;
                        }
                    }
                    return false;
                }
            });
        }

        return fileList;
    }

    public static long getFileSize(final String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    public static long getDirSize(final String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return 0;
        }
        long size = 0;
        try {
            File file = new File(dirPath);
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size += getDirSize(fileList[i].getAbsolutePath());
                } else {
                    size += fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // 传统的＊和？匹配
    public static File[] getFilesClassic(final String dir, final String pattern) {
        if (TextUtils.isEmpty(dir) || TextUtils.isEmpty(pattern)) {
            return null;
        }
        StringBuilder builder = new StringBuilder("^");
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char word = pattern.charAt(i);
            if (state == 0) {
                if (word == '?') {
                    builder.append('.');
                } else if (word == '*') {
                    builder.append(".*");
                } else {
                    builder.append("\\Q");
                    builder.append(word);
                    state = 1;
                }
            } else {
                if (word == '?' || word == '*') {
                    builder.append("\\E");
                    state = 0;
                    if (word == '?') {
                        builder.append('.');
                    } else {
                        builder.append(".*");
                    }
                } else {
                    builder.append(word);
                }
            }
        }
        if (state == 1) {
            builder.append("\\E");
        }
        builder.append('$');
        ArrayList<File> list = null;
        try {
            Pattern p = Pattern.compile(builder.toString());
            list = filePattern(new File(dir), p);
        } catch (Exception e) {
            return null;
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        File[] rtn = new File[list.size()];
        list.toArray(rtn);
        return rtn;
    }

    private static ArrayList<File> filePattern(File file, Pattern p) {
        if (file == null) {
            return null;
        } else if (file.isFile()) {
            Matcher fMatcher = p.matcher(file.getName());
            if (fMatcher.matches()) {
                ArrayList<File> list = new ArrayList<File>();
                list.add(file);
                return list;
            }
        }
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                ArrayList<File> list = new ArrayList<File>();
                for (File f : files) {
                    if (p.matcher(f.getName()).matches()) {
                        list.add(f);
                    }
                }
                return list;
            }
        }
        return null;
    }

    // 李建衡：读文件。如果无法读出数据，就返回null
    public static String fileRead(String file) {
        if (TextUtils.isEmpty(file)) {
            return null;
        }

        byte[] buffer = fileRead(new File(file));

        if (buffer == null) {
            return null;
        }

        return new String(buffer);
    }

    public static String fileRead(String file, String charsetName) {
        if (TextUtils.isEmpty(file) || TextUtils.isEmpty(charsetName)) {
            return null;
        }

        byte[] buffer = fileRead(new File(file));

        if (buffer == null) {
            return null;
        }

        try {
            return new String(buffer, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] fileReadBytes(String file) {
        if (TextUtils.isEmpty(file)) {
            return null;
        }

        return fileRead(new File(file));
    }

    public static byte[] fileRead(File file) {
        byte[] buffer = null;

        if (!file.exists()) {
            return buffer;
        }

        FileInputStream fis;

        try {
            fis = new FileInputStream(file);
            try {
                int len = fis.available();
                buffer = new byte[len];
                fis.read(buffer);
            } finally {
                fis.close();
            }
        } catch (Throwable e) { // new byte有可能是OOM异常，要用Throwable跟IOException一起捕获
            e.printStackTrace();
            return null;
        }

        return buffer;
    }

    // 李建衡：尝试获取文件的编码格式
    // 和线上版本是一致的，DataInputStream读取文件时，会把utf的标志过滤掉，
    // 也就是说前两个字节没了，直接判断0xfeff是不行的
    public static String getFileCharset(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        File file = new File(filename);
        FileInputStream fileIS;

        try {
            fileIS = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        DataInputStream dis = null;
        dis = new DataInputStream(fileIS);

        int p = 0;
        byte buf[] = null;

        try {
            buf = new byte[512];
        } catch (OutOfMemoryError oom) {
            try {
                dis.close();
                fileIS.close();
            } catch (Exception e1) {
            }

            return null;
        }

        try {
            byte a = dis.readByte();
            byte ba = dis.readByte();
            dis.read(buf);

            p = buf[0]<<8 + buf[1];
        } catch (IOException e) {
            try {
                dis.close();
                fileIS.close();
            } catch (Exception e1) {
            }

            return null;
        }

        String code = null;

        if (p == 0xefbb || p == 0xfeff || StringCodec.isUTF8(buf)) {
            code = "UTF-8";
        } else if (p == 0xfffe) {
            code = "Unicode";
        } else {
            code = "GBK";
        }

        try {
            dis.close();
            fileIS.close();
        } catch (Exception e1) {
        }

        return code;
    }

    public static boolean fileWrite(String file, String data) {
        if (TextUtils.isEmpty(file) || data == null) {
            return false;
        }

        try {
            return fileWrite(new File(file), data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean fileWrite(String file, byte[] data) {
        if (TextUtils.isEmpty(file) || data == null) {
            return false;
        }

        try {
            return fileWrite(new File(file), data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    public static boolean fileWrite(File file, byte[] data) {
        FileOutputStream fos;

        try {
            file.createNewFile();
            fos = new FileOutputStream(file);

            try {
                fos.write(data);
            } finally {
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean createFile(File file) {
        try {
            if (file != null) {
                if(file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 对目录进行统一化处理。
     * 这里很乱，
     * 三星手机的/sdcard0/与emulated/0是同一目录
     * 在华为上，/sdcard又是真实的，没有emulated
     * @param oldPath
     * @return
     */
    public static String unifyMusicFilePath(String oldPath){
        String realPath = oldPath;
        if(oldPath.contains("/sdcard")){
            realPath = oldPath.replace("/sdcard", "/emulated/"); //此处把用户改为sdcard这种链接目录的路径，更换为真实目录emulated目录结构。
            LogMgr.d("DirectoryScanner", "unifyMusicFilePath-->oldPath:"+oldPath+",newPath"+realPath);
            if(!isExist(realPath)){ //有些手机上，没有emulated存在，则保持原来路径
                realPath = oldPath;
            }
        }
        if(false==noLegacyFlag&&oldPath.contains("/legacy/")){
            realPath = oldPath.replace("/legacy/", "/0/"); //特别情况下，需要把legacy目录转回为/0目录。主用户目录，都是android支持多用户闹的
            if(!isExist(realPath)){ //有些手机可能没有对应目录，则保持原状态
                realPath = oldPath;
                noLegacyFlag=true;
            }
        }
        return realPath;
    }
    private static boolean noLegacyFlag=false;//用于提高上面unifyMusicFilePath的执行性能。这个函数调用比较多

    public static boolean assertExist(String assertPath){
        InputStream is = null;
        try {
            is = App.getInstance().getAssets().open(assertPath);
            int length = is.available();
            if (length > 0) {
                return true;
            }
        } catch (Exception e) {
           //
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                Log.d(TAG, "closeInputStream Exception");
            }
        }
        return false;
    }

    public static Uri getUriForFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName(), file);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                uri = null;
            }
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    public static boolean deleteDirectory(final File dir) {
        if (dir == null) {
            return false;
        }
        // dir doesn't exist then return true
        if (!dir.exists()) {
            return true;
        }
        // dir isn't a directory then return false
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    private static boolean copyOrMoveFile(final File srcFile,
                                          final File destFile,
                                          final boolean isMove) {
        if (srcFile == null || destFile == null) return false;
        // srcFile equals destFile then return false
        if (srcFile.equals(destFile)) return false;
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile()) return false;
        if (destFile.exists()) {
            if (!destFile.delete()) {// unsuccessfully delete then return false
                return false;
            }
        }
        if (!createOrExistsDir(destFile.getParentFile())) return false;
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile))
                    && !(isMove && !deleteSingleFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean deleteSingleFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    private static boolean writeFileFromIS(final File file,
                                           final InputStream is) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyDir(final String srcDirPath,
                                  final String destDirPath) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    public static boolean copyDir(final File srcDir,
                                  final File destDir) {
        return copyOrMoveDir(srcDir, destDir, false);
    }

    private static boolean copyOrMoveDir(final File srcDir,
                                         final File destDir,
                                         final boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        // destDir's path locate in srcDir's path then return false
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return false;
        }
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        if (destDir.exists()) {
            if (!deleteDirectory(destDir)) {// unsuccessfully delete then return false
                return false;
            }
        }
        if (!createOrExistsDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                if (!copyOrMoveFile(file, oneDestFile, isMove)) {
                    return false;
                }
            } else if (file.isDirectory()) {
                if (!copyOrMoveDir(file, oneDestFile, isMove)) {
                    return false;
                }
            }
        }
        return !isMove || deleteDirectory(srcDir);
    }


    public static boolean isLocalFile(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return false;
        }
        if (uri.startsWith("http://") || uri.startsWith("https://")) {
            return false;
        }
        try {
            String url = URLDecoder.decode(uri);
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return false;
            }
        } catch (Exception e) {
            //
        }
        return true;
    }


    /**
     * 合并文件
     * @param fpaths
     * @param resultPath
     * @return
     */
    public static boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || TextUtils.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return new File(fpaths[0]).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i ++) {
            files[i] = new File(fpaths[i]);
            if (TextUtils.isEmpty(fpaths[i]) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);
        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.length; i ++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                resultFileChannel.write(ByteBuffer.wrap("\r\n".getBytes()));
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < fpaths.length; i++) {
            files[i].delete();
        }
        return true;
    }

    //filePath 必须在非 app 目录下
    public static void openTxtFile(@NonNull Context context, @NonNull String filePath) {
        Uri uri = getUriForFile(context, new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//必须,否则目标程序无法读取到文件
        context.startActivity(intent);
    }
}
