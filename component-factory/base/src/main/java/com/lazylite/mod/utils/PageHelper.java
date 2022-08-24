package com.lazylite.mod.utils;

/**
 * 必须在主线程中调用
 * <p>
 * Created by lzf on 2022/1/4 4:06 下午
 */
public class PageHelper {
    private static final int UN_PAGE = -1;
    private static final int TYPE_PAGE = 1;
    private static final int TYPE_INDEX = 2;

    private final int pageType;
    private final int initIndex;
    private final int initSize;

    private int currentIndex = UN_PAGE;

    /**
     * @param usePage true 采用分页形式：initIndex表示页码，size表示一页多少数据；
     *                false 采用索引形式：index是开始加载数据的索引，size是index之后加载多少数据
     */
    public PageHelper(int initIndex, int size, boolean usePage) {
        this.initIndex = initIndex;
        this.initSize = size;
        if (usePage) {
            pageType = TYPE_PAGE;
        } else {
            pageType = TYPE_INDEX;
        }
    }

    public int getIntIndex() {
        return initIndex;
    }

    public int getInitSize(){
        return initSize;
    }

    /**
     * 注意，这个返回的是当前已经加载出的数据的 当前页数/数据最后的索引
     */
    public int getCurrentPage() {
        if (UN_PAGE == currentIndex) {
            return initIndex;
        }
        return currentIndex;
    }

    public int getNextPage() {
        if (UN_PAGE == currentIndex) {
            return initIndex;
        }
        if (TYPE_PAGE == pageType) {
            return currentIndex + 1;
        } else {
            return currentIndex + initSize;
        }
    }

    /**
     * 初始化 成功
     */
    public void initLoadSuccess() {
        setCurrentIndex(initIndex);
    }

    /**
     * 刷新 成功
     */
    public void refreshLoadSuccess() {
        initLoadSuccess();
    }

    private void setCurrentIndex(int index){
        currentIndex = index;
    }

    /**
     * 加载更多 成功
     */
    public void loadMoreSuccess() {
        if (UN_PAGE == currentIndex) {
            setCurrentIndex(initIndex);
        } else {
            if (TYPE_PAGE == pageType) {
                setCurrentIndex(currentIndex+1);
            } else {
                setCurrentIndex(currentIndex + initSize);
            }
        }
    }
}
