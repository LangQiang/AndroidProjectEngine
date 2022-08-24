package com.lazylite.mod.utils.psrc;


import androidx.annotation.NonNull;


public class PsrcOptional {

    private static final String TAG = PsrcOptional.class.getSimpleName();

    public static final int DEFAULT_POSITION = -1;

    /**
     * @param rootPsrc 当前页的节点名称,比如"首页"，"推荐"
     * @param position 当前节点的位置，比如"首页"在四个tab中是第0个,-1表示不缀位置
     * @return 新的psrcInfo
     */
    public static PsrcInfo buildRoot(String rootPsrc, int position){
        PsrcInfo psrcInfo = new PsrcInfo();
        psrcInfo.add(rootPsrc, position);
        return psrcInfo;
    }

    /**
     * @param curNodePos 目标页的位置
     * @param curNodeName 目标页名称
     */
    public static PsrcInfo build(@NonNull PsrcInfo psrcInfo, @NonNull String curNodeName, int curNodePos){
        PsrcInfo info = new PsrcInfo(psrcInfo);
        info.add(curNodeName, curNodePos);
        return info;
    }

    /**
     * @param curNodePos 目标页的位置
     */
    public static PsrcInfo build(@NonNull PsrcInfo psrcInfo, int curNodePos){
        PsrcInfo info = new PsrcInfo(psrcInfo);
        info.add("", curNodePos);
        return info;
    }

    /**
     * 为weex等设计
     * @param psrc 完整的psrc
     * @param lcn 完整的psrc
     * @param curNodePos 目标页的位置
     */
    public static PsrcInfo buildForWeex(String psrc, String lcn, int curNodePos){
        PsrcInfo psrcInfo = new PsrcInfo();
        psrcInfo.addForWeex(psrc, lcn, curNodePos);
        return psrcInfo;
    }

    /**
     * 用于补充当前页标题
     */
    public static PsrcInfo checkAndAddName(PsrcInfo curInfo, String curName){
        PsrcInfo info = new PsrcInfo(curInfo);
        info.checkAndAddName(curName);
        return info;
    }

    /**
     * 所有获取lcn的地方都调用此方法，不要直接info.getLcn();
     */
    public static Nullable get(PsrcInfo info){
        if (info == null){
            return new NullInfo();
        } else {
            return info;
        }
    }

    /**
     * 将PsrcInfo信息拼接到scheme中
     * */
    public static String buildSchemeWithPsrcInfo(@NonNull String scheme, PsrcInfo psrcInfo) {
        if (psrcInfo == null) {
            return scheme;
        }
        if (scheme.contains(PsrcConstant.KEY_LCN)
                || scheme.contains(PsrcConstant.KEY_PSRC)){
            return scheme;
        }
        StringBuilder sb = new StringBuilder(scheme);
        if (scheme.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append(PsrcConstant.KEY_LCN).append("=").append(get(psrcInfo).getLcn())
                .append("&").append(PsrcConstant.KEY_LCN_INDEX).append("=").append(get(psrcInfo).getLastNodePos())
                .append("&").append(PsrcConstant.KEY_PSRC).append("=").append(get(psrcInfo).getPsrc());
        return sb.toString();
    }
}
