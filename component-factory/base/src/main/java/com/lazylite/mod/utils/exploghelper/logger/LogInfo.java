package com.lazylite.mod.utils.exploghelper.logger;

import com.lazylite.mod.utils.psrc.PsrcInfo;

import java.util.List;


public class LogInfo<T> {

    private List<T> mList;

    private PsrcInfo mPsrcInfo;

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public PsrcInfo getPsrcInfo() {
        return mPsrcInfo;
    }

    public void setPsrcInfo(PsrcInfo psrcInfo) {
        mPsrcInfo = psrcInfo;
    }
}
