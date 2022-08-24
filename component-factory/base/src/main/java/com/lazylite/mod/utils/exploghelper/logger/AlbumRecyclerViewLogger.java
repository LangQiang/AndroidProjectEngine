//package com.lazylite.mod.utils.exploghelper.logger;
//
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.lazylite.mod.utils.psrc.PsrcInfo;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//
//public class AlbumRecyclerViewLogger extends BaseRecyclerViewLogger<AlbumDetailInfo>{
//
//    public AlbumRecyclerViewLogger(RecyclerView listView, List data, PsrcInfo psrcInfo) {
//        super(listView, data, psrcInfo);
//    }
//
//    @Override
//    protected JSONObject buildJsonInfo(AlbumDetailInfo item) throws JSONException {
//        JSONObject jsonObject = new JSONObject();
//        if (item == null) {
//            return jsonObject;
//        }
//        jsonObject.put(Constant.LOC, item.getPos());
//        jsonObject.put(Constant.DTYPE, 2);
//        jsonObject.put(Constant.ID, item.getId());
//        jsonObject.put(Constant.NAME, item.getName());
//        return jsonObject;
//    }
//
//    @Override
//    public void addItem2List() {
//        if (mData == null) {
//            return;
//        }
//        for (int i = mFirstVisibleItem; i <= mLastVisibleItem; i++) {
//            if (i < mData.size()) {
//                AlbumDetailInfo info = mData.get(i);
//                info.setPos(i);
//                addItem(info);
//            }
//        }
//    }
//}
