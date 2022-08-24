package com.lazylite.mod.log;

import android.text.TextUtils;

import com.lazylite.mod.utils.KuwoUrl;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.RequestInfo;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.crypt.Base64Coder;

/**
 * @author qyh
 * email：yanhui.qiao@kuwo.cn
 * @date 2021/5/31.
 * description：日志上传
 */
public class LogUploadManager implements IUploadLog {

    private static final LogUploadManager INSTANCE = new LogUploadManager();

    private LogUploadManager() {
    }

    public static LogUploadManager getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean logRealMsg(String strAct, String strContent, int ret) {
        return sendLog(strAct, strContent, ret, false);
    }

    @Override
    public boolean saveRealMsg(String resourceType, String strContent, int retValue) {
        return sendLog(resourceType, strContent, retValue, true);
    }

    private boolean sendLog(String resourceType, String strContent, int retValue, boolean isSave) {
        if (TextUtils.isEmpty(resourceType)) {
            if (LogMgr.isDebug) {
                LogMgr.w("[logRealMsg] bad params");
            }
            return false;
        }
        StringBuilder stringBuilder = formatRealtimeLog(resourceType, strContent, retValue);
        if (stringBuilder == null) {
            return false;
        }
        String content = stringBuilder.toString();
        stringBuilder.setLength(0);  //释放对字符串的引用

        if (NetworkStateUtil.isAvailable()) {
            uploadLog(false, content);
            return true;
        } else {
            DiskLogUtils.writeLogByFileName(content);
            return false;
        }
    }

    @Override
    public boolean asynSendOfflineLog(String content, int arg1) {

        return false;
    }

    @Override
    public void checkLocalLogAndUpload() {
        if (NetworkStateUtil.isAvailable()) {
            DiskLogUtils.readFileContent((content) -> {
                uploadLog(true, content);
            });
        }
    }

    // todo 这个后期要根据业务需求重新定义字段
    private StringBuilder formatRealtimeLog(String actValue, String strContent, final int retValue) {
        if (TextUtils.isEmpty(actValue) || TextUtils.isEmpty(strContent)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ACT:").append(actValue)
                .append("||ERR:").append(retValue)
                .append("|Content:").append(strContent);
        return sb;
    }

    private void uploadLog(boolean isLocalLog, String content) {
        if (content == null) return;
        LogMgr.e("uplog content===" + content);
        String s = Base64Coder.encodeString(content, "utf-8", null);
        String safeUrl = KuwoUrl.UrlDef.LOGURL.getSafeUrl();
        RequestInfo requestInfo = RequestInfo.newPost(safeUrl, s.getBytes());
        KwHttpMgr.getInstance().getKwHttpFetch().asyncPost(requestInfo, responseInfo -> {
            if (responseInfo.getCode() == 200 && isLocalLog) {
                DiskLogUtils.deleteCache();
            }
        });
    }
}
