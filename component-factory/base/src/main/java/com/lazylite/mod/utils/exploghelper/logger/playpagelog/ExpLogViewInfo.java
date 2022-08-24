//package com.lazylite.mod.utils.exploghelper.logger.playpagelog;
//
//import android.view.View;
//
//import com.lazylite.mod.utils.psrc.PsrcInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ExpLogViewInfo {
//
//    private View view;
//
//    private List<ExpLog> expLogList;
//
//    private PsrcInfo psrcInfo;
//
//    public ExpLogViewInfo(View view, List<ExpLog> expLogs, PsrcInfo psrcInfo) {
//        this.view = view;
//        this.expLogList = expLogs;
//        this.psrcInfo = psrcInfo;
//    }
//
//    public static List<ExpLog> buildExpLogs(PlayAdInfo.PlayBigImgAdBean playBigImgAdBean) {
//        List<ExpLog> ret = new ArrayList<>();
//        if (playBigImgAdBean != null) {
//            ExpLog expLog = new ExpLog(0, -1, playBigImgAdBean.id, playBigImgAdBean.title);
//            ret.add(expLog);
//        }
//
//        return ret;
//    }
//
//    public static <T> List<ExpLog> buildExpLogs(List<T> list) {
//        List<ExpLog> ret = new ArrayList<>();
//        if (list != null) {
//            int loc = 0;
//            for (T t: list) {
//                int dType = -1;
//                long id = 0;
//                String name = "unknown";
//                if (t instanceof PlayAdInfo.PlayListAdBean) {
//                    id = ((PlayAdInfo.PlayListAdBean) t).id;
//                    name = ((PlayAdInfo.PlayListAdBean) t).title;
//                } else if (t instanceof KwGameInfo) {
//                    name = ((KwGameInfo) t).name;
//                } else if (t instanceof AlbumDetailInfo) {
//                    id = ((AlbumDetailInfo) t).getId();
//                    name = ((AlbumDetailInfo) t).getName();
//                }
//                ExpLog expLog = new ExpLog(loc++, dType, id, name);
//                ret.add(expLog);
//            }
//        }
//        return ret;
//    }
//
//    public View getView() {
//        return view;
//    }
//
//    public List<ExpLog> getExpLogList() {
//        return expLogList;
//    }
//
//    public PsrcInfo getPsrcInfo() {
//        return psrcInfo;
//    }
//
//    public static class ExpLog {
//
//        private int loc;
//
//        private int dType;
//
//        private long id;
//
//        private String name;
//
//        public ExpLog(int loc, int dType, long id, String name) {
//            this.loc = loc;
//            this.dType = dType;
//            this.id = id;
//            this.name = name;
//        }
//
//        public int getLoc() {
//            return loc;
//        }
//
//        public int getDType() {
//            return dType;
//        }
//
//        public long getId() {
//            return id;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }
//
//}
