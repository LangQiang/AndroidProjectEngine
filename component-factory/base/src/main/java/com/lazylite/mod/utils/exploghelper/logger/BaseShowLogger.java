package com.lazylite.mod.utils.exploghelper.logger;

import android.view.View;
import android.widget.AbsListView;

import com.lazylite.mod.utils.psrc.PsrcInfo;
import com.lazylite.mod.utils.psrc.PsrcOptional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 抽象出需要滚动露出日志的统计类
 */

public abstract class BaseShowLogger<T> {

    public static final int IDLE_TIME = 500;

    public long mLastScrollTime;
    public int mFirstVisibleItem;
    public int mLastVisibleItem;

    protected PsrcInfo mPsrcInfo;

    private Map<String, LogInfo<T>> mLogDatas;

    public boolean isFirst = true;

    public BaseShowLogger(PsrcInfo psrcInfo) {
        mPsrcInfo = psrcInfo;
        mLogDatas = new HashMap<>();
    }

    public void setPsrcInfo(PsrcInfo psrcInfo) {
        mPsrcInfo = psrcInfo;
    }

    public void sendLog() {
        if (mLogDatas.size() == 0) {
            return;
        }
        Set<Map.Entry<String, LogInfo<T>>> entrySet = mLogDatas.entrySet();
        for (Map.Entry<String, LogInfo<T>> entry : entrySet) {
            LogInfo<T> logInfo = entry.getValue();
            PsrcInfo psrcInfo = logInfo.getPsrcInfo();
            List<T> mItemList = logInfo.getList();
            if (mItemList == null || mItemList.size() == 0) {
                continue;
            }
            try {
                JSONArray jsonArray = new JSONArray();
                for (T item : mItemList) {
                    JSONObject jsonObject = buildJsonInfo(item);
                    if (jsonObject != null) {
                        jsonArray.put(jsonObject);
                    }
                }
                mItemList.clear();
                if (jsonArray.length() == 0) {
                    continue;
                }

            } catch (Exception e) {
            }
        }
        mLogDatas.clear();
    }

    protected abstract JSONObject buildJsonInfo(T item) throws JSONException;

    protected abstract void addItem2List();

    public void addItem(T item) {
        addItem(item, mPsrcInfo);
    }

    public void addItem(T item, PsrcInfo psrcInfo) {
        if (item == null) {
            return;
        }
        String psrc = PsrcOptional.get(psrcInfo).getPsrc();
        LogInfo<T> info = mLogDatas.get(psrc);
        if (info == null) {
            info = new LogInfo<>();
            mLogDatas.put(psrc, info);
        }
        info.setPsrcInfo(psrcInfo);
        List<T> items = info.getList();
        if (items != null) {
            if (!items.contains(item)) {
                items.add(item);
            }
        } else {
            List<T> itemList = new ArrayList<>();
            itemList.add(item);
            info.setList(itemList);
        }
    }


    public void onScrollStateChanged(int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mLastScrollTime = System.currentTimeMillis();
                checkToAddItem();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mLastScrollTime = System.currentTimeMillis();
                break;
        }
    }

    protected abstract View getView();

    private void checkToAddItem() {
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - mLastScrollTime >= IDLE_TIME) {
                    addItem2List();
                }
            }
        }, IDLE_TIME);
    }

}
