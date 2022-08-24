package com.lazylite.mod.config;

/**
 * Created by lzf on 6/18/21 2:38 PM
 */
public interface IConfDef {
    String SEC_DEFAULT = "";
    String	SEC_APP								= "appconfig";
    String KEY_APP_UNIQUE_ID = "unique_id";
    String KEY_APP_UUID = "app_uuid";
    String SEC_LR_LOGIN = "lr_lite_login";

    String 	KEY_LOGIN_UID						= "login_uid";
    String KEY_LOGIN_SID = "login_sid";
    String KEY_LOGIN_EVENT_ID = "login_event_id";

    String KEY_ENTRUST_HOST = "entrust_host";

    //开屏隐私弹窗
    String KEY_PROTOCOL_DIALOG_IS_SHOWED = "key_protocol_dialog_is_showed";
}
