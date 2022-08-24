package com.lazylite.mod.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.lazylite.mod.App;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.log.LogMgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


// by haiping 所有得到的路径末尾都已经有了"/"符号了，切记！
public final class KwDirs {

    public static final String SEC_DOWNLOAD = "download";
    public static final String KEY_PREF_DOWNLOAD_SAVE_PATH = "save_path";

    /**
     *
     */
    public static final String EXT_READONLY_FLAG = "readonly|";
    // 大于100的不缓存
    public static final int
            SD_ROOT = 0,
            EXT_ROOT = 1, // 外置SD卡
            HOME = 2, // sdcard/KwTingShu
            DOWNLOAD = 103,
            CACHE = 4, // cn.kuwo.com.tme.index.base.cache存储数据的位置
            LYRICS = 5, // 歌词目录，旧版资源也在
            CODECS = 6, // 下载的解码器，这版内置mp3 wma aac ape flac，暂时没有下载功能
            PLAYCACHE = 7, // 播放缓存
            CRASH = 8, // 崩溃堆栈文件
            TEMP = 9, // 一个单纯的缓存路径，每次主动退出程序都会清空
            LOG = 10,
            BACKUP = 11,
            SETTING = 12,
            RING = 13, // 铃声剪辑
            SKIN = 14,
            WELCOME = 15,
            PICTURE = 16, // 图片目录，旧版的相关图片也会在
            UPDATE = 17, // SD卡Download目录
            GAMES = 18, // 游戏
            PUSH = 19, // 推送
            DEFDOWNLOAD = 20, // 默认下载目录
            LIBS = 21, // so文件存放位置
            CRASHBAK = 22, // 崩溃日志发送之后会删掉，在这个目录备份一份
            DATABASE = 23, // 有些机器内置数据库创建失败，会在这里创建数据库
            SEARCH = 24,
            QUKU_CACHE = 25,
            AUTODOWN_CACHE = 26,
            OFFLINE_CACHE = 27,
            CONFIG = 28, // 存放服务器配置
            DEFEXDOWNLOAD = 29, //外置sd卡默认歌曲下载目录
            KSINGACCOMLYRIC = 30,
            ONLINE_FONT = 31,    //在线字体保存目录
            POSTER = 32,
            KUWO_SHARE_PIC = 33, //用来保存到本地的图片目录
            QRCODE = 34,//酷我二维码保存路径
            KSING_CHORUS_CHO = 35,
            KSING_CHORUS_HALF = 36,
            KSING_SHOWOFF_CARD = 37,
            SCREENAD = 38,//开屏素材
            KSING_STORY_ACV = 39,//滤镜文件保存路径
            UPGRADE = 40, //升级高音质临时文件
            UPGRADEWAV = 41, //升级高音质临时文件
            UPGRADEFINGER = 42, //升级高音质临时文件
            GIFT = 43,          // 存放礼物
            FRESCO = 44, //fresco
            WX_LOCAL = 45,
            PUBLISH_CHAPTER_LOG = 46,
            DOWN_SAMPLE = 47,
            MAX_ID = 48; //保持最大


    // 所有得到的路径末尾都已经有了"/"符号了，切记！
    public static String getDir(final int dirID) {
        int savePos = dirID < 100 ? dirID : dirID - 100;
        String dirPath = dirs[savePos];
        if (dirPath != null) {
            return dirPath;
        }
        dirPath = "";
        switch (dirID) {
            case SD_ROOT:
                dirPath = SD_ROOTPATH;
                break;
            case EXT_ROOT:
                dirPath = "";
                List<String> paths = getStoragePaths(App.getInstance().getApplicationContext());
                if (paths != null && paths.size() >= 2) {
                    for (String path : paths) {
                        if (path != null && !path.equals(getFirstExterPath())) {//非手机自带存储根目录，即为sdcard根目录
                            File pathFile = new File(path);
                            try {
                                String realPath = pathFile.getCanonicalPath();
                                if (realPath.equals(path)) {
                                    dirPath = path;
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case HOME:
                dirPath = HOME_PATH;
                break;
            case KUWO_SHARE_PIC:
                dirPath = USER_VISIBLE_ROOT_PATH + "KUWO_PIC";
                break;
            case CACHE:
                dirPath = HOME_PATH_FOR_HIDE + "data";
                break;
            case QUKU_CACHE:
                dirPath = HOME_PATH_FOR_HIDE + "data" + File.separator + "QUKU_CACHE";
                break;
            case DOWNLOAD:
                dirPath = ConfMgr.getStringValue(SEC_DOWNLOAD, KEY_PREF_DOWNLOAD_SAVE_PATH, null);
                if (TextUtils.isEmpty(dirPath)) {
                    dirPath = USER_VISIBLE_ROOT_PATH + "music"; // music目录用户可见
                }
                break;
            case LYRICS:
                dirPath = HOME_PATH + "lyrics";
                break;
            case CODECS:
                dirPath = HOME_PATH_FOR_HIDE + "codec";
                break;
            case PLAYCACHE:
                dirPath = HOME_PATH_FOR_HIDE + "playcache";
                break;
            case AUTODOWN_CACHE:
                dirPath = HOME_PATH_FOR_HIDE + "playcache" + File.separator + "autodown";
                break;
            case OFFLINE_CACHE:
                dirPath = HOME_PATH_FOR_HIDE + "playcache" + File.separator + "offline";
                break;
            case CRASH:
                dirPath = HOME_PATH_FOR_HIDE + "crash";
                break;
            case TEMP:
                dirPath = HOME_PATH_FOR_HIDE + "temp";
                break;
            case LOG:
                dirPath = HOME_PATH + "log";
                break;
            case BACKUP:
                dirPath = HOME_PATH_FOR_HIDE + "important";
                break;
            case SETTING:
                dirPath = HOME_PATH_FOR_HIDE + "setting";
                break;
            case RING:
                dirPath = HOME_PATH_FOR_HIDE + "ring";
                break;
            case SKIN:
                dirPath = HOME_PATH_FOR_HIDE + "skin";
                break;
            case CONFIG:
                dirPath = HOME_PATH_FOR_HIDE + "config";
                break;
            case WELCOME:
                dirPath = HOME_PATH + "welcome";
                break;
            case QRCODE:
                dirPath = HOME_PATH + "QRCode";
                break;
            case SEARCH:
                dirPath = HOME_PATH + "search";
                break;
            case PICTURE:
                dirPath = HOME_PATH + "picture"; // 歌手大图目录用户可见
                break;
            case UPDATE:
                dirPath = SD_ROOTPATH + "Download";
                break;
            case GAMES:
                dirPath = HOME_PATH_FOR_HIDE + "games";
                break;
            case PUSH:
                dirPath = HOME_PATH_FOR_HIDE + "push";
                break;
            case DEFDOWNLOAD:
                dirPath = USER_VISIBLE_ROOT_PATH + "music";
                break;
            case DEFEXDOWNLOAD:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    dirPath = getDir(EXT_ROOT) + "Android/data/" + App.getInstance().getPackageName();
                } else {
                    dirPath = getDir(EXT_ROOT) + "YuanXi/music";
                }
                break;
            case LIBS:
                File dir = App.getInstance().getFilesDir();
                if (dir != null) {
                    dirPath = KwFileUtils.getFilePath(dir.getAbsolutePath()) + File.separator + "lib";
                } else {
                    dir = App.getInstance().getDir("lib", Context.MODE_PRIVATE);
                    if (dir != null) {
                        dirPath = KwFileUtils.getFilePath(dir.getAbsolutePath()) + File.separator + "lib";
                    } else {
                        dirPath = HOME_PATH_FOR_HIDE + "lib";
                    }
                }
                break;
            case CRASHBAK:
                dirPath = HOME_PATH + "crashbak";
                break;
            case DATABASE:
                dirPath = HOME_PATH_FOR_HIDE + "database";
                break;
            case KSINGACCOMLYRIC:
                dirPath = HOME_PATH_FOR_HIDE + "kwsing/lyrics";
                break;
            case ONLINE_FONT:
                dirPath = HOME_PATH_FOR_HIDE + "fonts";
                break;
            case POSTER:
                dirPath = HOME_PATH + "poster";
                break;
            case KSING_CHORUS_CHO:
                dirPath = HOME_PATH_FOR_HIDE + "kwsing/chorus_cho";
                break;
            case KSING_CHORUS_HALF:
                dirPath = HOME_PATH_FOR_HIDE + "kwsing/chorus_half";
                break;
            case KSING_SHOWOFF_CARD:
                dirPath = HOME_PATH_FOR_HIDE + "kwsing/KuwoMusic";
                break;
            case SCREENAD:
                dirPath = HOME_PATH_FOR_HIDE + "screenad";
                break;
            case KSING_STORY_ACV:
                dirPath = HOME_PATH_FOR_HIDE + "kwsing/story_acv";
                break;
            case UPGRADE:
                dirPath = HOME_PATH_FOR_HIDE + "upgrade";
                break;
            case UPGRADEWAV:
                dirPath = HOME_PATH_FOR_HIDE + "upgrade/wav";
                break;
            case UPGRADEFINGER:
                dirPath = HOME_PATH_FOR_HIDE + "upgrade/finger";
                break;
            case GIFT:
                dirPath = HOME_PATH_FOR_HIDE + "gift";
                break;
            case FRESCO:
                dirPath = HOME_PATH_FOR_HIDE + "fresco";
                break;
            case WX_LOCAL:
                dirPath = SD_INNER_ROOTPATH + "wxLocal";
                break;
            case PUBLISH_CHAPTER_LOG:
                dirPath = HOME_PATH + "publish/chapter";
                break;
            case DOWN_SAMPLE:
                dirPath = getExternalStorageDirectory() + "元惜/试音素材";
                break;
        } // switch

        if (!TextUtils.isEmpty(dirPath) && !dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }

        if (dirID < 100) {
            dirs[savePos] = dirPath;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dirPath;
    }

    private KwDirs() {
    }

    private static String[] dirs = new String[MAX_ID];
    /**
     * data/data 目录下的根路径
     */
    private static final String SD_INNER_ROOTPATH = getKuWoRootPathInner().getAbsolutePath() + File.separator;
    private static final String HOME_INNER_PATH = SD_INNER_ROOTPATH + "YuanXi" + File.separator;
    private static final String HOME_INNER_PATH_FOR_HIDE = HOME_INNER_PATH + (new File(SD_INNER_ROOTPATH + File.separator + "kuwo.zhp").exists() ? "" : ".");

    private static final String SD_ROOTPATH = getKuWoRootPath().getAbsolutePath() + File.separator;
    private static final String USER_VISIBLE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "YuanXi" + File.separator;
    private static final String HOME_PATH = SD_ROOTPATH + "YuanXi" + File.separator;
    private static final String HOME_PATH_FOR_HIDE = HOME_PATH + (new File(SD_ROOTPATH + File.separator + "kuwo.zhp").exists() ? "" : ".");

    //获取优先级最高的外部存储根目录
    public static String getFirstExterPath() {
        try {
            return Environment.getExternalStorageDirectory().getPath();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList<String>();

        String firstPath = getFirstExterPath();
        LogMgr.d("SDCARD", "First externalSdcard:" + firstPath);
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                LogMgr.d("SDCARD", line);
                // 将常见的linux分区过滤掉
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.startsWith("/data/media"))
                    continue;
                if (line.contains("/mnt/media_rw/") && line.contains("/dev/block/"))
                    continue;
                if (line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data")
                        || line.contains("tmpfs") || line.contains("shell")
                        || line.contains("root") || line.contains("acct")
                        || line.contains("proc") || line.contains("misc")
                        || line.contains("obb")) {
                    continue;
                }
                //下面是三星一款手机的外卡信息
                ///dev/block/vold/179:9 /mnt/media_rw/extSdCard vfat rw,dirsync,seclabel,nosuid,nodev,noexec,noatime,nodiratime,uid=1023,gid=1023,fmask=0007,dmask=0007,allow_utime=0020,codepage=cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
                ///mnt/media_rw/extSdCard /storage/extSdCard    sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=unified 0 0
                //下面是标准内存sdcard信息
                ///data/media /storage/emulated/0 sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=legacy,reserved=20MB 0 0
                ///data/media /storage/emulated/legacy sdcardfs rw,seclabel,nosuid,nodev,relatime,uid=1023,gid=1023,derive=legacy,reserved=20MB 0 0

                if (line.contains("fat") || line.contains("fuse") || (line
                        .contains("ntfs")) || line.contains("/extSdCard")) {

                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        String path = columns[1];
                        if (path != null && !SdList.contains(path) && path.toLowerCase().contains("sd"))
                            SdList.add(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!SdList.contains(firstPath)) {
            SdList.add(firstPath);
        }
        return SdList;
    }

    /**
     * 此方法会返回几个根目录（手机内部储存根目录/sdcard1根目录/sdcard2根目录等等）。
     * <p>
     * 2015.09.15增加的获取外置sd卡的方法，此方法按android版本使用不同的方式，获取外置sd卡路径
     *
     * @param cxt
     * @return
     */
    public static List<String> getStoragePaths(Context cxt) {
        List<String> pathsList = null;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
            pathsList = getAllExterSdcardPath();
        } else {
            if (cxt == null) {
                return getAllExterSdcardPath();
            }
            pathsList = new ArrayList<String>();
            StorageManager storageManager = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
            try {
                if (storageManager != null) {
                    Method method = StorageManager.class.getDeclaredMethod("getVolumePaths");
                    if (method != null) {
                        method.setAccessible(true);
                        Object result = method.invoke(storageManager);
                        if (result != null && result instanceof String[]) {
                            String[] pathes = (String[]) result;
                            StatFs statFs;
                            for (String path : pathes) {
                                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                                    statFs = new StatFs(path);
                                    if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
                                        pathsList.add(path);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
            if (pathsList.size() == 0) {
                pathsList = getAllExterSdcardPath();
            }
        }
        return pathsList;
    }

    public static interface OnSdcardCheckListener {
        public static int REASON_NOSPACE = 11; //没空间可写入
        public static int REASON_USERCANCEL = 12; //用户在修改目录对话框点击取消
        public static int REASON_NOEXIST = 13; //用户所选目录创建失败，不存在目录
        public static int REASON_NO_PERMISSION = 1001; //无权限
        public static int REASON_CONTEXT_NONE = 1002; //无权限

        void onSdcardAvailable(boolean isChanged, String newDownPath);

        void onSdcardUnavailable(int reason);
    }

    public static long MIN_SPACE = 32 * 1024 * 1024;  //剩余空间最小限值是32MB


    // TODO: 2021/5/31 qyh
    /**
     * 判断当前下载目录，是否可用，可用就直接调用所传入listener的onSdcardAvailable回调，
     * 不可用首先显示一键设置下载目录对话框，用户重新设置后，如果还不可用，则执行onSdcardUnavailable并给出原因。
     *
     * @param listener
     */

//    public static void checkSdcardForDownload(final OnSdcardCheckListener listener) {
//        KwDebug.assertPointer(listener);
//        if (MainActivity.getInstance() == null) {
//            if (listener != null) {
//                listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_CONTEXT_NONE);
//            }
//            return;
//        }
//        if (!Permission.checkSelfPermission(MainActivity.getInstance(), new String[]{App.PERMISSION_WRITE_STORAGE})) {
//            Permission.requestPermissions(MainActivity.getInstance(), 1, new String[]{App.PERMISSION_WRITE_STORAGE}, new SimpleCallback() {
//                @Override
//                public void onSuccess(int requestCode) {
//                    checkSdcardForDownload(listener);
//                }
//
//                @Override
//                public void onFail(int requestCode, String[] permissions, int[] grantResults) {
//                    if (listener != null) {
//                        listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NO_PERMISSION);
//                    }
//                    KwToast.showSysToast("无法获取【读取】权限");
//                }
//            }, new KwPermissionUI(MainActivity.getInstance()));
//            return;
//        }
//
//        final String defDownPath = getDir(DEFDOWNLOAD);
//        final String defExDownPath = getDir(DEFEXDOWNLOAD);
//
//        String curDownPath = getDir(DOWNLOAD);
//        File curDownFile = new File(curDownPath);
//        if (defDownPath.equals(curDownPath)) { //当前下载目录与默认目录是同一个目录，则只判断剩余空间。
//            if (curDownFile.exists() && curDownFile.isDirectory()) {
//                if (getPathSpaceSize(curDownPath) > MIN_SPACE) {    //如果剩余空间大于10MB，则直接使用，否则提示用户空间不足
//                    listener.onSdcardAvailable(false, curDownPath);
//                } else {
//                    //下载目录内存不足提示
//                    listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOSPACE);
//                }
//            } else {
//                //默认目录都不存在，则创建一次
//                if (curDownFile.mkdirs()) {
//                    if (getPathSpaceSize(curDownPath) > MIN_SPACE) {    //如果剩余空间大于10MB，则直接使用，否则提示用户空间不足
//                        listener.onSdcardAvailable(false, curDownPath);
//                    } else {
//                        //下载目录内存不足提示
//                        listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOSPACE);
//                    }
//                } else {
//                    //创建目录失败，则直接提示用户目录不可用，判断外存可不可用，可用则弹框一键切换到外存，不存在则直接提示用户目录不可用退出
//                    boolean isExSdcardCanUse = false;
//                    if (!TextUtils.isEmpty(defExDownPath)) {
//                        File exDownPath = new File(defExDownPath);
//                        if (exDownPath.exists() && exDownPath.isDirectory()) {
//                            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
//                                File tmpFile = new File(exDownPath, "tmp.log");
//                                try {
//                                    if (tmpFile.exists()) {
//                                        if (tmpFile.delete()) {    //删除成功，则证明目录也是可读写的，直接判断空间
//                                            isExSdcardCanUse = true;
//                                        }
//                                    } else {
//                                        if (tmpFile.createNewFile()) {
//                                            tmpFile.delete();                        //能创建文件，说明下载目录可读写，则执行listener
//                                            isExSdcardCanUse = true;
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                }
//                            } else {
//                                isExSdcardCanUse = true; //4.4系统以前手机外置sd卡直接使用，不用判断是否要写入
//                            }
//                        }
//                    }
//                    if (isExSdcardCanUse) {
//                        showSdcardSelectDialog(false, listener);
//                    } else {
//                        listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOEXIST);
//                    }
//                }
//            }
//        } else { //下面是下载目录与默认目录不相同情况
//            if (curDownFile.exists() && curDownFile.isDirectory()) {
//                //如果当前下载目录存在
//                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
//                    File tmpFile = new File(curDownFile, "tmp.log");
//                    try {
//                        if (tmpFile.exists()) {
//                            if (tmpFile.delete()) {    //删除成功，则证明目录也是可读写的，直接判断空间
//                                //判断剩余空间
//                                if (getPathSpaceSize(curDownPath) > MIN_SPACE) {    //如果剩余空间大于10MB，则直接使用，否则提示用户空间不足
//                                    listener.onSdcardAvailable(false, curDownPath);
//                                } else {
//                                    //下载目录内存不足提示
//                                    listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOSPACE);
//                                }
//                            } else {
//                                //删除失败，则显示目录不可用对话框换目录
//                                showSdcardSelectDialog(true, listener);
//                            }
//                        } else {
//                            if (tmpFile.createNewFile()) {
//                                tmpFile.delete();                        //能创建文件，说明下载目录可读写，则执行listener
//                                //判断剩余空间
//                                if (getPathSpaceSize(curDownPath) > MIN_SPACE) {    //如果剩余空间大于10MB，则直接使用，否则提示用户空间不足
//                                    listener.onSdcardAvailable(false, curDownPath);
//                                } else {
//                                    //下载目录内存不足提示
//                                    listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOSPACE);
//                                }
//                            } else {
//                                //创建失败，则显示目录不可用对话框换目录
//                                showSdcardSelectDialog(true, listener);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        //异常失败，则显示目录不可用对话框换目录
//                        showSdcardSelectDialog(true, listener);
//                    }
//                } else {
//                    //判断剩余空间
//                    if (getPathSpaceSize(curDownPath) > MIN_SPACE) {    //如果剩余空间大于10MB，则直接使用，否则提示用户空间不足
//                        listener.onSdcardAvailable(false, curDownPath);
//                    } else {
//                        //下载目录内存不足提示
//                        listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_NOSPACE);
//                    }
//                }
//            } else {
//                //显示目录不可用对话框
//                showSdcardSelectDialog(true, listener);
//            }
//        }
//    }

    // TODO: 2021/5/31  qyh

    /**
     * 弹对话框提示用户，当前选择目录不可用，可一键切换到另一个存储空间，
     *
     * @param ExtoDef：标识当前是不是从外置sd卡转内存存储空间（true)，如果为false，则是内存有问题在向外存转换
     * @param listener：回调函数
     */
//    public static void showSdcardSelectDialog(final boolean ExtoDef, final OnSdcardCheckListener listener) {
//        KwDebug.assertPointer(listener);
//
//        if (BaseApp.getApplication() == null) {
//            listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_USERCANCEL);
//        } else {
//            String dlgContent = "因为系统限制，歌曲无法下载到所选路径，可修改为：";
//            if (ExtoDef) {
//                dlgContent += getDir(DEFDOWNLOAD) + "，继续下载";
//            } else {
//                dlgContent += getDir(DEFEXDOWNLOAD) + "，继续下载";
//            }
//            Activity activity = MainActivity.getInstance();
//            if (activity != null && !activity.isFinishing()) {
//                KwDialog dialog = new KwDialog(activity, -1);
////				dialog.setLogoIcon(R.drawable.logo);
////				dialog.setMessage(dlgContent);
//                dialog.setOnlyTitle(dlgContent);
//                dialog.setOkBtn("立即修改", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String newDownPath = null;
//                        if (ExtoDef) {
//                            newDownPath = getDir(DEFDOWNLOAD);
//                        } else {
//                            newDownPath = getDir(DEFEXDOWNLOAD);
//                        }
//                        ConfMgr.setStringValue(ConfDef.SEC_DOWNLOAD, ConfDef.KEY_PREF_DOWNLOAD_SAVE_PATH, newDownPath, true);
//                        listener.onSdcardAvailable(true, newDownPath);
//                    }
//                });
//                dialog.setCancelBtn("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onSdcardUnavailable(OnSdcardCheckListener.REASON_USERCANCEL);
//                    }
//                });
//                dialog.show();
//            }
//        }
//    }
    public static long getPathSpaceSize(String path) {
        StatFs stat = null;
        try {
            stat = new StatFs(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long availableSize = availableBlocks * blockSize;
        return availableSize;
    }

    public static File getKuWoRootPath() {
        File file = App.getInstance().getExternalFilesDir(null);
        if (file == null) {
            file = App.getInstance().getFilesDir();
        }
        return file;
    }

    /**
     * 结尾带 "/" ；如果无存储权限会重定位到 app/data目录。
     */
    public static String getExternalStorageDirectory() {
        try {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } catch (SecurityException e) {
            return getKuWoRootPath().getAbsolutePath() + "/";
        }
    }

    /**
     * 应用数据存储目录
     *
     * @return
     */
    public static File getKuWoRootPathInner() {
        File file = App.getInstance().getFilesDir();
        return file;
    }

    public static String getUserTsRootPath() {
        return USER_VISIBLE_ROOT_PATH;
    }
}
