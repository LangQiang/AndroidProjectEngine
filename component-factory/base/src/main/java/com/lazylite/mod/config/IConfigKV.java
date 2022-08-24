package com.lazylite.mod.config;

public interface IConfigKV {

    void init();

    boolean setBoolValue(final String strSec, final String strKey, final boolean bVal, final boolean bNotifyChanged);

    boolean getBoolValue(final String strSec, final String strKey, final boolean bDefVal);

    String getStringValue(final String strSec, final String strKey, final String strDefVal);

    boolean setStringValue(final String strSec, final String strKey, final String strVal, final boolean bNotifyChanged);

    int getIntValue(final String strSec, final String strKey, final int iDefVal);

    boolean setIntValue(final String strSec, final String strKey, final int iVal, final boolean bNotifyChanged);

    long getLongValue(final String strSec, final String strKey, final long iDefVal);

    boolean setLongValue(final String strSec, final String strKey, final long iVal, final boolean bNotifyChanged);

    float getFloatValue(final String strSec, final String strKey, final float fDefVal);

    boolean setFloatValue(final String strSec, final String strKey, final float fVal, final boolean bNotifyChanged);
}
