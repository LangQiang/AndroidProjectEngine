package com.lazylite.mod.http.mgr;

import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.config.IConfDef;
import com.lazylite.mod.utils.ApplicationUtils;

import java.util.Map;

/**
 * Created by lzf on 2022/3/21 16:16
 */
public class AccountHeaderPolicy implements ICommHeaderPolicy {
    public static final String KEY_UID = "X-Auth-Uid";
    public static final String KEY_SESSION = "X-Auth-Ticket";
    public static final String KEY_EVENT = "X-Auth-EventId";
    @Override
    public boolean isCanUse() {
        return ApplicationUtils.isPlayMusicProcess();
    }

    @Override
    public boolean configParams(Map<String, String> oldParams) {
        final String uid = ConfMgr.getLongValue(IConfDef.SEC_LR_LOGIN, IConfDef.KEY_LOGIN_UID, -1) + "";
        final String session = ConfMgr.getStringValue(IConfDef.SEC_LR_LOGIN, IConfDef.KEY_LOGIN_SID, "");
        final String eventId = ConfMgr.getStringValue(IConfDef.SEC_LR_LOGIN, IConfDef.KEY_LOGIN_EVENT_ID, "");
        oldParams.put(KEY_UID, uid);
        oldParams.put(KEY_SESSION, session);
        oldParams.put(KEY_EVENT, eventId);
        return true;
    }
}
