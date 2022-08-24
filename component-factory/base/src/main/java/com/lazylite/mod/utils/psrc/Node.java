package com.lazylite.mod.utils.psrc;

import java.io.Serializable;

public class Node implements Serializable {

    private static final long serialVersionUID = 3569759542219564695L;

    /**
     * 节点名称，用于单个节点
     */
    private String nodeName;
    /**
     * 单个节点对应的索引
     */
    private int position = -1;
    /**
     * 完整的lcn
     * 为了兼容weex，h5交互。打破设计规则的字段
     */
    private String lcnName;

    public Node(String nodeName, int position){
        this.nodeName = nodeName;
        this.position = position;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLcnName() {
        return lcnName;
    }

    public void setLcnName(String lcnName) {
        this.lcnName = lcnName;
    }
}
