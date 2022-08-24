//package com.lazylite.mod.utils.exploghelper.logger.playpagelog;
//
//import android.graphics.Rect;
//
//import androidx.annotation.NonNull;
//import androidx.core.widget.NestedScrollView;
//
//import com.lazylite.mod.log.nts.NtsLog;
//import com.lazylite.mod.utils.psrc.PsrcOptional;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//
////UI线程中调用方法
//public class PlayPageScrollViewLogger {
//
//    private LinkedHashMap<Integer, ExpLogViewInfo> allExpViews = new LinkedHashMap<>();
//
//    private LinkedHashMap<Integer, ExpLogViewInfo> logInfoList = new LinkedHashMap<>();
//
//    private Rect bounds = new Rect();
//
//    private NestedScrollView mScrollView;
//
//    public PlayPageScrollViewLogger(@NonNull NestedScrollView scrollView) {
//        this.mScrollView = scrollView;
//    }
//
//    /**
//     * 第一个参数NestedScrollView v:是NestedScrollView的对象
//     *     第二个参数:scrollX是目前的（滑动后）的X轴坐标
//     *     第三个参数:ScrollY是目前的（滑动后）的Y轴坐标
//     *     第四个参数:oldScrollX是之前的（滑动前）的X轴坐标
//     *     第五个参数:oldScrollY是之前的（滑动前）的Y轴坐标
//     * */
//
//    public void attach() {
//        if (mScrollView == null) {
//            return;
//        }
//        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                checkAll();
//            }
//        });
//    }
//
//    public void addExpLogViewInfo(@ExpConstants int expKey, ExpLogViewInfo expLogViewInfo) {
//        allExpViews.put(expKey, expLogViewInfo);
//        check(expKey, expLogViewInfo);
//    }
//
//    public void send() {
//        if (logInfoList.size() == 0) {
//            return;
//        }
//        for (Map.Entry<Integer, ExpLogViewInfo> integerExpLogViewInfoEntry : logInfoList.entrySet()) {
//            ExpLogViewInfo expLogViewInfo = integerExpLogViewInfoEntry.getValue();
//            NtsLog.send(new NtsLog.Properties(NtsLog.ACT_NTS_EXP)
//                    .putLCN(PsrcOptional.get(expLogViewInfo.getPsrcInfo()).getLcn())
//                    .putPsrc(PsrcOptional.get(expLogViewInfo.getPsrcInfo()).getPsrc())
//                    .putKeyValue("EXP", buildExpJson(expLogViewInfo.getExpLogList()))
//            );
//        }
//        logInfoList.clear();
//    }
//
//    private String buildExpJson(List<ExpLogViewInfo.ExpLog> expLogViewInfo) {
//        try {
//            JSONArray jsonArray = new JSONArray();
//            for (ExpLogViewInfo.ExpLog expLog : expLogViewInfo) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.putOpt("LOC", expLog.getLoc());
//                jsonObject.putOpt("ID", expLog.getId());
//                jsonObject.putOpt("DTYPE", expLog.getDType());
//                jsonObject.putOpt("NAME", expLog.getName());
//                jsonArray.put(jsonObject);
//            }
//
//            return jsonArray.toString();
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    private void check(int expKey, ExpLogViewInfo expLogViewInfo) {
//        if (logInfoList.size() == allExpViews.size()) {
//            return;
//        }
//        mScrollView.getHitRect(bounds);
//        if (expLogViewInfo.getView().getLocalVisibleRect(bounds)) {
//            logInfoList.put(expKey, expLogViewInfo);
//        }
//    }
//
//    private void checkAll() {
//        if (logInfoList.size() == allExpViews.size()) {
//            return;
//        }
//        for (Map.Entry<Integer, ExpLogViewInfo> integerExpLogViewInfoEntry : allExpViews.entrySet()) {
//            ExpLogViewInfo next = integerExpLogViewInfoEntry.getValue();
//            if (next.getView().getLocalVisibleRect(bounds)) {
//                logInfoList.put(integerExpLogViewInfoEntry.getKey(), next);
//            }
//        }
//    }
//
//}
