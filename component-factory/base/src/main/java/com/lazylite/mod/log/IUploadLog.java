package com.lazylite.mod.log;

/**
 * @author qyh
 * email：yanhui.qiao@kuwo.cn
 * @date 2021/5/31.
 * description：
 */
public interface IUploadLog {

    //发送实时日志
    boolean logRealMsg(String strAct, String strContent, int ret);

    // 保存日志
    boolean saveRealMsg(String strAct, String strContent, int ret);

    boolean asynSendOfflineLog(String content, int arg1);

    void checkLocalLogAndUpload();
}
