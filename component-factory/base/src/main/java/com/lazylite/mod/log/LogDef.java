package com.lazylite.mod.log;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.utils.KwDate;

//by zhangchao
/*
日志模块使用说明

1.日志系统
 	- 有两套日志：LogDef.OldLogType 和 LogDef.LogType
 	- 两套日志在日志格式、服务器接口上有所不同
 	- 旧日志现在依然要发，请使用 OldLogMgr发送/记录
 	
2.调试日志
	- 使用LogMgr记录

3.本地日志
	- 使用LogMgr记录
	
4.实时日志
	- 一般日志使用LogMgr发送即可
	- ServiceLevel 日志可使用 ServiceLevelTimeOutLogger协助计时
	- 如果需要获知指定日志的发送成功/失败情况，请在 LogSenderObserver 中进行处理

-- by zc 20130914
*/

@SuppressLint("DefaultLocale")
public final class LogDef {
    public static final String ACT = "|ACT:";

    public static final long CLIENT_LOG_MAX_READ_SIZE = 5 * 1024 * 1024;        // 5M
    public static final long CLIENT_LOG_MAX_STORE_DAYS = 7;                        // 本地日志最多保留7天

    public static class ResourceResult {
        public static final int RESULT_SUCCESS = 0;    // 加载成功，成功是指用户体验到了才算成功，如看到了图片和歌词，听到了歌
        public static final int RESULT_LOAD_FAIL = 1;    // 资源到本地后加载失败，比如图像加载失败，歌词解析失败，歌曲格式错误等
        public static final int RESULT_USER_CANCEL = 2;    // 用户取消，不需要此次结果了，比如用户切歌造成上首歌的歌词图片不再需要
        public static final int RESULT_CACHE = 3;    // 资源的来源是用户本地的缓存，成功了才算
        public static final int RESULT_USER_LYRIC = 4;    // 用户本地的歌词
        public static final int RESULT_NO_RESOURCE = 5;    // 服务器上无此资源，网络请求是成功的，但是服务器明确表示无此资源
        public static final int RESULT_NET_CONNECT_ERR = 6;    // 连接失败
        //public static final int RESULT_CONNECT_TIMEOUT 		= 7;	// Android不需要，连接超时只是连接错误的一种
        public static final int RESULT_NET_IO_ERR = 8;    // 网络读写失败
        //public static final int RESULT_NET_IO_TIMEOUT 		= 9;	// 网络读写超时，Android下只是网络读写失败的一种，不需要
        public static final int RESULT_NET_NO_NETWORK = 10;    // 没有可用的网络
        public static final int RESULT_NET_OTHER_ERR = 99;    // 其它类型的网络错误 状态码非200
        public static final int RESULT_BIG_PIC_LIST_FAILED = 101;    // 歌手大图的列表获取失败造成取歌手大图失败
        public static final int RESULT_SERVER_RESPONSE_NOT_FOUND = 404;    // 服务器返回404
        public static final int RESULT_UNKNOWN_ERR = 900;    // 其它未知错误
        public static final int RESULT_NET_ERR_GETRESPONSECODE = 901;    // 在获取Http返回状态值时出错
        public static final int RESULT_REQ_KEY_NONE = 903;    // 请求的关键字段为空
        public static final int RESULT_SERVICELEVEL_TIMEOUT = 999;    // serviceLvel中的超时（成功但超过预期时间了）
        public static final int RESULT_VIP_CHECKFAIL = 201;   //VIP权限检查没通过
        public static final int RESULT_NO_SDCARD = 202;   //下载相关的没有sd卡存在
        public static final int RESULT_NO_SPACE = 203;   //下载相关的没有剩余空间
        public static final int RESULT_ANTISTEALING_FAILED = 204;   //下载相关防盗链获取失败
        public static final int RESULT_FILE_IO_FAILED = 205;   //下载相关的文件IO错误
        public static final int RESULT_ONLYWIFI_FAILED = 206;   //下载相关的仅在WIFI下联网失败

        public static final int RESULT_JSON_ERROR = 207;//json解析失败

        public static boolean isFailed(final int retValue) {
            if (retValue == ResourceResult.RESULT_SUCCESS
                    || retValue == ResourceResult.RESULT_USER_CANCEL
                    || retValue == ResourceResult.RESULT_CACHE
                    || retValue == ResourceResult.RESULT_USER_LYRIC
                    || retValue == ResourceResult.RESULT_REQ_KEY_NONE
                    || retValue == ResourceResult.RESULT_SERVER_RESPONSE_NOT_FOUND
                    || retValue == ResourceResult.RESULT_NO_RESOURCE
                    || retValue == ResourceResult.RESULT_BIG_PIC_LIST_FAILED) {
                return false;
            }
            return true;
        }
    }

    ;

    // 实时日志发送级别
    private static final long SEND_LOG_NO = 1;            //不发送此条日志
    private static final long SEND_LOG_2G = 2;            //只在2G网络发送
    private static final long SEND_LOG_2G_3G = 3;        //只在2G或者3G或者其他运营商网络发送
    private static final long SEND_LOG_WIFI = 4;        //只在WiFi下发送
    private static final long SEND_LOG_WAHTEVER = 5;    //不管什么网络，全部发送

    public static enum LogType {

        SHOW_LOG,        //秀场统计
        AppStart,            //启动日志*
        DEVICE_INFO,        //设备信息日志*
        ERROR_LOG,            //错误日志*
        PLAY,                //播放失败时的特殊日志 最终会转换成ERROR_LOG
        LISTPAGE,            //曲库日志*
        RECOMMPAGE,            //推荐日志*
        RADIO,                //获取电台歌曲*
        CLOUD,                //云同步日志*
        DLMUSIC,            //主动下载 SL指标统计*
        PLAY_MUSIC,            //播歌日志
        PLAY_FIRST_MUSIC,   //测试第一首歌
        BIGPIC,
        SMALLPIC,
        LYRIC,
        LIST,
        SEARCHPAGE,
        CRASH,                    //崩溃*
        JNI_CRASH,              //jni层代码崩溃
        CRASH_EXCEPT,            //非主进程和播放进程崩溃
        REGISTER,                //注册
        LOGIN,                //登录
        YIGUAN_CRASH,       //易观崩溃
        AIRUI_CRASH,      //艾瑞崩溃
        XIAOMI_CRASH,     //小米崩溃
        SEARCH_TIME,        //search时间
        DOWN_MUSIC,            //歌曲下载成功的统计，非SL指标
        TEST_SPEED,                //网络测速
        FAVORITE_SONGLIST, //我的收藏歌单用到的
        LOGIN_ERROR, //我的收藏歌单对登录状态和登录信息不同步的统计
        PLAY_STOP, //自动播放暂停日志统计

        FEATURE_LOG,                //二级日志
        CAR_PLAY,       //车载音乐 add by wangxudong
        REQUEST_IPDOMAIN,//发送IP请求
        DIGEST_QUALITY,// 用于统计不同类型点击质量（譬如歌单点击）

        //--------------------听歌识曲统计------------------------
        LISTEN_KUGOU,  //酷狗听歌识曲统计日志
        REGISTER_EX,  //用户注册成功或失败统计日志，（此为商务计算需要）
        LOGIN_EX,        //用户登录成功或失败统计日志（此为商务计算需要）
        //--------------------个性化统计-------------------------
        RD_DOWNLOAD_MUSIC,// 下载歌曲
        RD_DELETE_DOWNLOAD,// 删除下载歌曲
        RD_FAVOR_MUSIC,// 喜欢歌曲
        RD_UNFAVOR_MUSIC,// 取消喜欢
        RD_NORCM,// 不感兴趣
        //--------------------个性化统计-------------------------

        // 搜索log
        SEARCHSONG,
        SEARCHCALLBACK,

        // 歌曲收费统计(VipNew)
        MUSIC_FEE,

        // 捆绑和精品推荐
        RECOMM_APP,
        // 捆绑和精品推荐
        CaiLing_ring,//用户点击统计
        SYS_FEEDBACK,// 获取手机信息

        // ×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
        // 添加日志类型时请注意：
        // 如果是实时日志，请相应修改 getLogSendType， 或在服务器配置是否发送；
        // ×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
        //-------网络状态离线日志------------------------------
        PROXY_IP,        //http获取到的默认代理信息日志
        NORIGHT,        //无版权日志
        //-------网络状态离线日志------------------------------

        CRASH_XC,
        PLAY_XC,
        PLAY_XC_ERR,
        ENTER_ROOM,
        CARD_SHARE,
        BUSINESS_CLICK,//商务的点击统计
        //运营统计日志
        OPERATION_STATISTICS,
        DDLOG,
        HIFI_LOG,
        //指纹识别
        FINGER_PRINT,
        QDSHOW,  //签到日志
        OTHER_O_LOG, //自定义日志

        //旧版听书运营日志
        TS_CLICK_LOG, //听书点击log
        TS_PAGE_LOG,  //听书展示log
        TS_SHOW_LOG,  //听书log
        TS_OTHER_LOG,  //听书其他的log
    }

    public static enum ShowFeatureType {//秀场feature类型
        SHOW_LOAD_SUCCESS,//页面加载成功
        ENTRY_ROOM_SUCCESS,//进房成功
        CLICK_ALL_CLASSIFY,//点击秀场大厅【全部分类】
        CLICK_MALL,//点击秀场大厅【商城】
        OPEN_MALL_VIP,//开通秀场大厅【商城】中的会员
        RECHARGE,//充值
        CLICK_RECHANGE,//点击“充值”
        CLICK_MY_FOCUS,//点击【我关注的】
        CLICK_PERSONAL_CENTER,//点击【个人中心】
        CLICK_LOOK_MORE,//点击【点击查看更多】
        FOCUS_SUCCESS,//关注主播成功
        CANCEL_FOCUS,//取消关注主播
        PUBLIC_CHAT,//直播间中的公聊
        PRIVATE_CHAT,//直播间中的私聊
        CLICK_AUDIENCE,//直播间中的点击【观众】
        CLICK_ARCHIVES,//点击观众-档案
        CLICK_PRIVATE_CHAT,//点击观众-私聊
        CLICK_PUBLIC_CHAT,//点击观众-公聊
        CLICK_SEND_GIFT,//点击观众-点歌控件
        CLICK_MORE,//点击更多
        CLICK_FANS_LIST,//点击“粉丝榜”
        OPEN_GUARD,//开通“守护”
        CLICK_HELP,//点击“帮助”
        SIGN_SUCCESS,//签到成功
        WHISPER_SUCCESS,//悄悄发言成功
        SEND_PLUME,//赠送羽毛
        CLICK_SELECT_SONG,//点击“点歌”控件
        SELECT_SONG_SUCCESS,//点歌成功

    }

    public static enum FeatureLogType {
        MYCHANNEL,
        RECOMMEND,
        LIBRARY,
        MVSHOW,
        SETRING,        //设置铃声
        SEARCHSONG,
        SEARCHVOICE,
        SIDETAB,
        ADDTODOWN,
        SCANLOCAL,
        FAVORITESONG,
        FAVORITELIST,
        NOWPLAY,
        LOGIN,
        REG,
        SHARESONG,
        CHANGESKIN,
        UNDEFAULTSKIN,
        SLEEPMODE,
        DESKLYRIC,
        LOCKSCREEN,
        DESKPLUGIN,
        PUSH,
        SHOW,
        ZHUANQU,
        BIGSETNEW,//聚合搜索大合集
        SHARE_TYPE,//分享类型，譬如歌单，专辑，MV
        POPULARIZEL, //底部换量
        TEMPAREAICONDLG,//符合创建快捷方式条件弹框
        TEMPAREAICONCLICK,//点击底部创建快捷方式条件弹框
        TEMPAREAICONCREATE,//创建快捷方式
        TEMPAREAICONUSE,//从快捷方式点击进入
        XMCLICK,//小米推送点击
        BIGCENTER,//大图开关关闭点击
    }


    public static boolean isServiceLog(final String act) {
        LogType logType = parseToLogType(act);
        if (logType == null) {
            return false;
        }
        return logType == LogType.AppStart
                || logType == LogType.ERROR_LOG
                || logType == LogType.PLAY
                || logType == LogType.LISTPAGE
                || logType == LogType.RECOMMPAGE
                || logType == LogType.RADIO
                || logType == LogType.CLOUD
                || logType == LogType.DLMUSIC
                || logType == LogType.BIGPIC
                || logType == LogType.SMALLPIC
                || logType == LogType.LYRIC
                || logType == LogType.LIST
                || logType == LogType.SEARCHPAGE
                || logType == LogType.REGISTER
                || logType == LogType.LOGIN
                || logType == LogType.CRASH
                || logType == LogType.CRASH_EXCEPT
                || logType == LogType.JNI_CRASH
                || logType == LogType.FEATURE_LOG
                || logType == LogType.CRASH_XC
                || logType == LogType.PLAY_XC_ERR;

    }

    public static LogType parseToLogType(final String act) {
        if (TextUtils.isEmpty(act)) {
            return null;
        }

        LogType logType = null;
        try {
            logType = LogType.valueOf(act);
        } catch (Exception e) {
            return null;
        }
        return logType;
    }


    public static boolean isErrorLog(final String act) {
        boolean flag = false;
        flag = act.equalsIgnoreCase(LogType.ERROR_LOG.name());
        return flag;
    }

    public static final String SEC_LOG = "Log";
    public static final String KEY_LOG_LAST_SEND_CL_SUCC_TIME = "last_send_clientlog_suc_time";
    public static final String KEY_LOG_LAST_SEND_CLIENTLOG_TIME = "last_send_clientlog_time";

    public static boolean isLastSendClientLogSuc1DaysAgo() {
        Long lastSendTime = ConfMgr.getLongValue(SEC_LOG, KEY_LOG_LAST_SEND_CL_SUCC_TIME, 0);
        return System.currentTimeMillis() - lastSendTime >= KwDate.T_MS_DAY;
    }

    public static void refreshLastSendClientLogSucTime() {
        Long lastSendTime = System.currentTimeMillis();
        ConfMgr.setLongValue(SEC_LOG, KEY_LOG_LAST_SEND_CL_SUCC_TIME, lastSendTime, false);
    }

    public static int getLastSendClientLogDaysToNow() {
        Long lastSendTime = ConfMgr.getLongValue(SEC_LOG, KEY_LOG_LAST_SEND_CLIENTLOG_TIME, System.currentTimeMillis());
        return (int) Math.ceil((double) (System.currentTimeMillis() - lastSendTime) / KwDate.T_MS_DAY);
    }

    public static void refreshLastSendClientLogTime() {
        Long lastSendTime = System.currentTimeMillis();
        ConfMgr.setLongValue(SEC_LOG, KEY_LOG_LAST_SEND_CLIENTLOG_TIME, lastSendTime, false);
    }


    public static String getAct(final String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        int n1 = content.indexOf(ACT);
        if (n1 < 0) {
            return null;
        }
        n1 += ACT.length();
        int n2 = content.indexOf("|", n1);
        if (n2 < 0) {
            n2 = content.indexOf(">", n1);
        }
        if (n2 < 0) {
            n2 = content.indexOf("}", n1);
        }
        if (n2 < 0) {
            return null;
        }

        return content.substring(n1, n2);
    }


    private LogDef() {

    }
}
