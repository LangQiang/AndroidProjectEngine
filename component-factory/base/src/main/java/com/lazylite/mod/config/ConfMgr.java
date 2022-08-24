package com.lazylite.mod.config;


public final class ConfMgr {

	// 所有配置相关的常量，定义在module：bridge中的 configDef.java中

    public static boolean getBoolValue(final String section, final String strKey, final boolean bDefVal) {
        return getConfImp().getBoolValue(section, strKey, bDefVal);
    }

    public static boolean setBoolValue(final String section, final String strKey, final boolean bVal, final boolean bNotifyChanged) {
        return getConfImp().setBoolValue(section, strKey, bVal, bNotifyChanged);
    }

    public static String getStringValue(final String section, final String strKey, final String strDefVal) {
        return getConfImp().getStringValue(section, strKey, strDefVal);
    }

    public static boolean setStringValue(final String section, final String strKey, final String strVal, final boolean bNotifyChanged) {
        return getConfImp().setStringValue(section, strKey, strVal, bNotifyChanged);
    }

    public static int getIntValue(final String section, final String strKey, final int iDefVal) {
        return getConfImp().getIntValue(section, strKey, iDefVal);
    }

    public static boolean setIntValue(final String section, final String strKey, final int iVal, final boolean bNotifyChanged) {
        return getConfImp().setIntValue(section, strKey, iVal, bNotifyChanged);
    }

    public static long getLongValue(final String section, final String strKey, final long iDefVal) {
        return getConfImp().getLongValue(section, strKey, iDefVal);
    }

    public static boolean setLongValue(final String section, final String strKey, final long iVal, final boolean bNotifyChanged) {
        return getConfImp().setLongValue(section, strKey, iVal, bNotifyChanged);
    }

    public static float getFloatValue(final String section, final String strKey, final float fDefVal) {
        return getConfImp().getFloatValue(section, strKey, fDefVal);
    }

    public static boolean setFloatValue(final String section, final String strKey, final float fVal, final boolean bNotifyChanged) {
        return getConfImp().setFloatValue(section, strKey, fVal, bNotifyChanged);
    }


    private static IConfigKV mConf = new ConfMMKVImpl();
    private static boolean mbInited = false;

    private static synchronized IConfigKV getConfImp() {
        if (!mbInited) {
            mConf.init();
            mbInited = true;
        }
        return mConf;
    }
}
