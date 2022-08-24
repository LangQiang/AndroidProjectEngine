package com.lazylite.mod.config;

import com.tencent.mmkv.MMKV;

public final class ConfMMKVImpl implements IConfigKV {
    //#前是组，#后是key
    private static final String SEPARATOR = "#";
    // 本地的
    private MMKV mDefaultMMKV;

    @Override
    public void init() {
        if (mDefaultMMKV == null) {
            mDefaultMMKV = MMKV.mmkvWithID("tme.anchor.tool.mmkv.defaultconfig", MMKV.MULTI_PROCESS_MODE);
        }
    }

    @Override
    public boolean setBoolValue(final String strSec, final String strKey, final boolean bVal, final boolean bNotifyChanged) {
        final String key = strSec + SEPARATOR + strKey;
        boolean ret = mDefaultMMKV.encode(key, bVal);
        if (ret && bNotifyChanged) {
            notifyConfigChanged(strSec, strKey);
        }
        return ret;
    }

    @Override
    public boolean getBoolValue(final String strSec, final String strKey, final boolean bDefVal) {
        String key = strSec + SEPARATOR + strKey;
        return mDefaultMMKV.decodeBool(key, bDefVal);
    }

    @Override
    public String getStringValue(final String strSec, final String strKey, final String strDefVal) {
        String key = strSec + SEPARATOR + strKey;
        return mDefaultMMKV.decodeString(key, strDefVal);
    }

    @Override
    public boolean setStringValue(final String strSec, final String strKey, final String strVal, final boolean bNotifyChanged) {
        final String key = strSec + SEPARATOR + strKey;
        boolean ret = mDefaultMMKV.encode(key, strVal);
        if (ret && bNotifyChanged) {
            notifyConfigChanged(strSec, strKey);
        }
        return ret;
    }

    @Override
    public int getIntValue(final String strSec, final String strKey, final int iDefVal) {
        String key = strSec + SEPARATOR + strKey;
        return  mDefaultMMKV.decodeInt(key, iDefVal);
    }

    @Override
    public boolean setIntValue(final String strSec, final String strKey, final int iVal, final boolean bNotifyChanged) {
        final String key = strSec + SEPARATOR + strKey;
        boolean ret = mDefaultMMKV.encode(key, iVal);
        if (ret && bNotifyChanged) {
            notifyConfigChanged(strSec, strKey);
        }
        return ret;
    }

    @Override
    public long getLongValue(final String strSec, final String strKey, final long iDefVal) {
        String key = strSec + SEPARATOR + strKey;
        return mDefaultMMKV.decodeLong(key, iDefVal);
    }

    @Override
    public boolean setLongValue(final String strSec, final String strKey, final long iVal, final boolean bNotifyChanged) {
        final String key = strSec + SEPARATOR + strKey;
        boolean ret = mDefaultMMKV.encode(key, iVal);
        if (ret && bNotifyChanged) {
            notifyConfigChanged(strSec, strKey);
        }
        return ret;
    }

    @Override
    public float getFloatValue(final String strSec, final String strKey, final float fDefVal) {
        String key = strSec + SEPARATOR + strKey;
        return mDefaultMMKV.decodeFloat(key, fDefVal);
    }

    @Override
    public boolean setFloatValue(final String strSec, final String strKey, final float fVal, final boolean bNotifyChanged) {
        final String key = strSec + SEPARATOR + strKey;
        boolean ret = mDefaultMMKV.encode(key, fVal);
        if (ret && bNotifyChanged) {
            notifyConfigChanged(strSec, strKey);
        }
        return ret;
    }

    private void notifyConfigChanged(final String strSec, final String strKey) {
//        Intent intent = new Intent(ConfDef.VAL_NOTIFY_CONFIG_CHANGED_INTENT);
//        intent.putExtra("key", strKey);
//        intent.putExtra("section", strSec);
//        intent.setComponent(new ComponentName(App.getInstance(),
//                "com.lazylite.bridge.protocal.config.ConfigChangedReceiver"));
//        App.getInstance().sendBroadcast(intent);
    }

}
