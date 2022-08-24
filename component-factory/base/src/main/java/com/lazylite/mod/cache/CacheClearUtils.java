package com.lazylite.mod.cache;

import com.lazylite.mod.utils.KwTimer;

/**
 * Created by xiaohan on 2016/7/13.
 */
public class CacheClearUtils {

    private final static int TIME_INTERVAL = 5*1000*60;
    private static KwTimer mTimer = null;

    public static void start(){
        if(mTimer==null){
            mTimer = new KwTimer(new KwTimer.Listener() {
                @Override
                public void onTimer(KwTimer timer) {
                    getCacheConfig();
                }
            });
        }
        if (!mTimer.isRunnig()){
            getCacheConfig();
            mTimer.start(TIME_INTERVAL);
        }

    }

    public static void stop(){
        if (mTimer!=null&&mTimer.isRunnig()){
            mTimer.stop();
        }
        mTimer = null;
    }

    private static void getCacheConfig(){
//        if (!NetworkStateUtil.isAvailable()||NetworkStateUtil.isOnlyWifiConnect()){
//            return;
//        }
//        KwThreadPool.runThread(KwThreadPool.JobType.NET, new Runnable() {
//            @Override
//            public void run() {
//                String url = UrlManagerUtils.getCacheClearUrl();
//                String result = HttpSession.getString(url);
//                if (TextUtils.isEmpty(result)){
//                    return;
//                }
//                long curNum;
//                try {
//                    curNum = Long.parseLong(result);
//                }catch (Exception e){
//                    return;
//                }
//
//                if (curNum<=0){
//                    return;
//                }
//                long lastNum = ConfMgr.getLongValue(ConfDef.SEC_PREF,ConfDef.KEY_CACHE_CLEAR_FLAG,ConfDef.VAL_CACHE_CLEAR_FLAG);
//                if (lastNum==ConfDef.VAL_CACHE_CLEAR_FLAG){
//                    ConfMgr.setLongValue(ConfDef.SEC_PREF,ConfDef.KEY_CACHE_CLEAR_FLAG,curNum,false);
//                    return;
//                }
//                if (curNum>lastNum){
//                    clearCache();
//                    ConfMgr.setLongValue(ConfDef.SEC_PREF,ConfDef.KEY_CACHE_CLEAR_FLAG,curNum,false);
//                }
//            }
//        });
    }

}
