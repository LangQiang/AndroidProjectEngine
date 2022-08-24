package com.lazylite.mod.widget.indicator.base;

/**
 * @author DongJr
 *
 * @date 2018/5/25.
 */
public interface IPagerTitle {
    /**
     * 换肤后的回调
     */
    void onSkinChanged();

    /**
     * 被选中
     */
    void onSelected(int index, int totalCount);

    /**
     * 未被选中
     */
    void onDeselected(int index, int totalCount);

    /**
     * @param leavePercent 离开的百分比
     * @param leftToRight 是否是从左到右
     */
    void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight);

    /**
     * @param enterPercent 进入的百分比
     * @param leftToRight 是否是从左到右
     */
    void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight);

    /**
     * 内容区域左边坐标
     */
    int getContentLeft();


    /**
     * 内容区域右边坐标
     */
    int getContentRight();

    /**
     *
     * 内容区域顶部坐标
     */
    int getContentTop();

    /**
     *
     * 内容区域底部坐标
     */
    int getContentBottom();

    /**
     * 设置普通颜色资源id
     */
    void setNormalColorRid(int colorRid);

    /**
     * 设置选中颜色资源id
     */
    void setSelectedColorRid(int colorRid);

}
