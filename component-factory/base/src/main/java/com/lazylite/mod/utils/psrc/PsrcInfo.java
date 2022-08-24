package com.lazylite.mod.utils.psrc;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PsrcInfo implements Nullable , Serializable {

    private static final long serialVersionUID = 5407592380150378240L;

    private List<Node> mNodeList = new ArrayList<>();

    PsrcInfo(){}

    PsrcInfo(PsrcInfo psrcInfo){
        if (psrcInfo != null){
            this.mNodeList = new ArrayList<>(psrcInfo.getNodeList());
        }
    }

    void add(String curNode, int position){
        Node node = new Node(curNode, position);
        mNodeList.add(node);
    }

    void addForWeex(String psrc, String lcn, int position){
        Node node = new Node(psrc, PsrcOptional.DEFAULT_POSITION);
        node.setLcnName(lcn);
        mNodeList.add(node);
        if (position != PsrcOptional.DEFAULT_POSITION) {
            mNodeList.add(new Node("", position));
        }
    }

    /**
     * 不要直接使用getLcn
     * @see PsrcOptional#get(PsrcInfo)
     */
    @Override
    @Deprecated
    public final String getLcn(){
        StringBuilder lcn = new StringBuilder();
        for (Node node : mNodeList){
            if (TextUtils.isEmpty(node.getNodeName()) && TextUtils.isEmpty(node.getLcnName())){
                continue;
            }
            String curLcn;
            //优先使用lcnName
            if (!TextUtils.isEmpty(node.getLcnName())){
                curLcn = node.getLcnName();
            } else{
                int position = node.getPosition();
                if (position >= 0){
                    curLcn = node.getNodeName() + "$" + node.getPosition();
                } else {
                    curLcn = node.getNodeName();
                }
            }
            if (TextUtils.isEmpty(curLcn)){
                continue;
            }
            if (lcn.length() == 0){
                lcn.append(curLcn);
            } else {
                lcn.append("->").append(curLcn);
            }
        }
        return lcn.toString();
    }

    @Override
    @Deprecated
    public String getPsrc() {
        StringBuilder lcn = new StringBuilder();
        for (Node node : mNodeList){
            if (TextUtils.isEmpty(node.getNodeName())){
                continue;
            }
            String curPsrc = node.getNodeName();
            if (lcn.length() == 0){
                lcn.append(curPsrc);
            } else {
                lcn.append("->").append(curPsrc);
            }
        }
        return lcn.toString();
    }

    @Override
    @Deprecated
    public int getLastNodePos() {
        if (mNodeList.isEmpty()){
            return PsrcOptional.DEFAULT_POSITION;
        } else {
            Node laseNode = mNodeList.get(mNodeList.size() - 1);
            // 最后节点没名字，直接修改为需要check的传递的节点名字
            if (TextUtils.isEmpty(laseNode.getNodeName())){
                return laseNode.getPosition();
            }
            return PsrcOptional.DEFAULT_POSITION;
        }
    }

    List<Node> getNodeList(){
        return mNodeList;
    }


    void checkAndAddName(String nodeName){
        if (mNodeList.isEmpty()){
            mNodeList.add(new Node(nodeName, PsrcOptional.DEFAULT_POSITION));
        } else {
            Node laseNode = mNodeList.get(mNodeList.size() - 1);
            // 最后节点没名字，直接修改为需要check的传递的节点名字
            if (TextUtils.isEmpty(laseNode.getNodeName())){
                laseNode.setNodeName(nodeName);
                return;
            }
            // 最后节点有名字，但不是需要check的传递的节点名字，需要追加上
            String lastNodeName = laseNode.getNodeName();
            String[] strArrary = lastNodeName.split("->");
            if (strArrary.length > 0) {
                String lastStr = strArrary[strArrary.length - 1];
                if (!lastStr.contains(nodeName)) {
                    mNodeList.add(new Node(nodeName, PsrcOptional.DEFAULT_POSITION));
                }
            }
        }
    }
}
